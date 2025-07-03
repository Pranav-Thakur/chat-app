package com.chatapp.service.impl;

import com.chatapp.ChatAppUtil;
import com.chatapp.config.JwtUtil;
import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.model.AppUserDO;
import com.chatapp.dao.model.LoginUserDO;
import com.chatapp.dao.service.AppUserRepoService;
import com.chatapp.dao.service.LoginUserRepoService;
import com.chatapp.exception.ChatAppException;
import com.chatapp.exception.ErrorCodes;
import com.chatapp.payload.request.AuthLoginRequest;
import com.chatapp.payload.request.AuthVerifyOtpRequest;
import com.chatapp.payload.response.AuthLoginResponse;
import com.chatapp.payload.response.AuthVerifyOtpResponse;
import com.chatapp.service.AuthService;
import com.chatapp.service.SmsSender;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private LoginUserRepoService loginUserRepoService;

    @Autowired
    private AppUserRepoService appUserRepoService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SmsSender smsSender;

    @Value("${chatapp.otp.length}")
    private int otpLength;

    @Value("${chatapp.otp.expiry.millis}")
    private long otpExpiryInMillis;

    @Value("${chatapp.jwttoken.expiry.millis}")
    private long jwtTokenExpiryInMillis;

    @Value("${chatapp.otp.verify.maxtry}")
    private int otpVerifyMaxtry;

    @Value("${chatapp.user.active.session}")
    private int maxActiveSessionUser;

    @Value("${chatapp.user.hardCheck.deviceId}")
    private boolean hardCheckDeviceId;

    @Value("${chatapp.twilio.activate}")
    private boolean twilioSmsSenderActivate;

    public AuthLoginResponse generateOtp(@NonNull AuthLoginRequest loginRequest) throws Exception {
        String phone = loginRequest.getPhoneNumber();
        if (phone == null || phone.length() != 10) {
            throw new ChatAppException("invalid phone number", ErrorCodes.INVALID_PHONE);
        }

        List<LoginUserDO> loginUserDOS = loginUserRepoService.getAppUserByPhoneAndStatus(phone, LoginUserStatus.OTP_GENERATED, 1);
        LoginUserDO loginUserDO = null;
        if (!loginUserDOS.isEmpty()) {
            loginUserDO = loginUserDOS.get(0);
            boolean expired = loginUserDO.getCreatedDate().plus(Duration.ofMillis(otpExpiryInMillis)).isBefore(LocalDateTime.now());
            boolean sameSrc = hardCheckDeviceId && !loginUserDO.getDid().equals(loginRequest.getDeviceId());
            if (!sameSrc) {
                // not from same src, treat it as new fresh request
                loginUserDO = null;
            } else if (expired) {
                // means previous session already timed out, so go on with new session as logindo = null
                loginUserDO.setOtp(null);
                loginUserDO.setStatus(LoginUserStatus.INACTIVE);
                loginUserDO.setPhone("NOT_NEEDED");
                loginUserRepoService.save(loginUserDO);
                loginUserDO = null;
            }
            // 3rd case session is active and sameSrc also, so resue it as logindo not null
        }

        String message = null;
        if (loginUserDO == null) {
            String otp = ChatAppUtil.randomOtp(otpLength);

            loginUserDO = new LoginUserDO();
            loginUserDO.setAgent(loginRequest.getAgent());
            loginUserDO.setPhone(phone);
            loginUserDO.setDid(loginRequest.getDeviceId());
            loginUserDO.setStatus(LoginUserStatus.OTP_GENERATED);
            loginUserDO.getInfo().putAll(loginRequest.getInfos());
            loginUserDO.getInfo().put("verifyTry", 0);
            loginUserDO.setOtp(jwtUtil.generateToken(otp, otpExpiryInMillis));
            loginUserDO = loginUserRepoService.save(loginUserDO);

            // You'd call actual SMS service here
            log.info("OTP for " + phone + " is: " + otp + ", sessionId: " + loginUserDO.getSessionId());
            message = "Otp sent successfully!";
        } else {
            // reusing this session to move forward
            log.info("resuing sessionId: " + loginUserDO.getSessionId());
            message = "Wait sometime to receive otp";
        }

        AuthLoginResponse authLoginResponse = new AuthLoginResponse();
        authLoginResponse.setSessionId(loginUserDO.getSessionId());
        authLoginResponse.setPhoneNumber(loginUserDO.getPhone());
        authLoginResponse.setMessage(message);

        return authLoginResponse;
    }

    // invalidate last active session for deviceId but no touch session just formed with sessionId
    private void invalidateLastActiveSession(@NonNull UUID sessionId, @NonNull String phone, @NonNull String deviceId) {
        try {
            List<LoginUserDO> loginUserDOS = loginUserRepoService.getAppUserByPhoneAndStatus(phone, LoginUserStatus.OTP_VERIFIED, maxActiveSessionUser);
            if (!loginUserDOS.isEmpty()) {
                for (LoginUserDO loginUserDO : loginUserDOS) {
                    if (!loginUserDO.getDid().equals(deviceId) || sessionId.equals(loginUserDO.getSessionId())) continue;

                    // do this if from same src ie same device id
                    loginUserDO.setStatus(LoginUserStatus.INACTIVE);
                    loginUserDO.setPhone("NOT_NEEDED");
                    loginUserRepoService.save(loginUserDO);
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    public AuthVerifyOtpResponse verifyOtp(@NonNull AuthVerifyOtpRequest req) throws Exception {
        LoginUserDO loginUserDO = loginUserRepoService.getAppUserById(req.getSessionId());
        if (loginUserDO == null)
            throw new ChatAppException("invalid session found, please try login again", ErrorCodes.INVALID_SESSION, HttpStatus.UNAUTHORIZED);

        else if (!loginUserDO.getDid().equals(req.getDeviceId()) || loginUserDO.getStatus() != LoginUserStatus.OTP_GENERATED) {
            loginUserDO.setOtp(null);
            loginUserDO.setStatus(LoginUserStatus.INACTIVE);
            loginUserDO.setPhone("NOT_NEEDED");
            loginUserDO = loginUserRepoService.save(loginUserDO);
            throw new ChatAppException("invalid session found, please try login again", ErrorCodes.INVALID_SESSION, HttpStatus.UNAUTHORIZED);
        }

        String phone = loginUserDO.getPhone();
        String otp = req.getOtp();
        String stored = null;

        try {
            stored = jwtUtil.extractSubject(loginUserDO.getOtp());
        } catch (ChatAppException e) {
            if (e.getErrorCode() == ErrorCodes.EXPIRY_AUTH_TOKEN) {
                loginUserDO.setOtp(null);
                loginUserDO.setStatus(LoginUserStatus.INACTIVE);
                loginUserDO.setPhone("NOT_NEEDED");
                loginUserDO = loginUserRepoService.save(loginUserDO);
                throw new ChatAppException("otp expired, try login again.", ErrorCodes.EXPIERED_OTP, HttpStatus.UNAUTHORIZED);
            }

            throw new ChatAppException("Error validating otp, please try login again.", ErrorCodes.ERROR_VALIDATE_OTP);
        }

        AuthVerifyOtpResponse response = new AuthVerifyOtpResponse();

        if (stored != null && stored.equals(otp)) {
            loginUserDO.setStatus(LoginUserStatus.OTP_VERIFIED);
            loginUserDO.setOtp(null);
            loginUserDO = loginUserRepoService.save(loginUserDO);

            response.setSessionId(req.getSessionId());
            response.setMessage("Otp Verified successfully!");
            response.setToken(jwtUtil.generateToken(loginUserDO.getSessionId().toString(), jwtTokenExpiryInMillis));

            try {
                AppUserDO appUserDO = appUserRepoService.getAppUserByPhone(phone);
                response.setUserId(appUserDO.getUserId().toString());

                // successfully verified otp, so clear the last active session for the same device id
                invalidateLastActiveSession(loginUserDO.getSessionId(), phone, loginUserDO.getDid());
            } catch (Exception e) {
                // do nothing
            }

        } else {
            // otp mismatch case
            int otpVerifyTried = (int) loginUserDO.getInfo().getOrDefault("verifyTry", 0);
            otpVerifyTried++;
            loginUserDO.getInfo().put("verifyTry", otpVerifyTried);
            if (otpVerifyTried >= otpVerifyMaxtry) {
                loginUserDO.setOtp(null);
                loginUserDO.setStatus(LoginUserStatus.INACTIVE);
                loginUserDO.setPhone("NOT_NEEDED");
                loginUserDO = loginUserRepoService.save(loginUserDO);
                throw new ChatAppException("max otp verify limit reached, please try login again", ErrorCodes.INVALID_SESSION, HttpStatus.UNAUTHORIZED);
            }

            loginUserDO = loginUserRepoService.save(loginUserDO);
            throw new ChatAppException("incorrect otp, please give correct otp.", ErrorCodes.INCORRECT_OTP);
        }

        log.info("OTP verify for " + phone + " is " + response);
        return response;
    }

    @Override
    public AuthVerifyOtpResponse refreshToken(@NonNull String deviceID, @NonNull String jwtToken) throws Exception {
        Pair<Boolean, Object> pair = jwtUtil.extractSubjectAndCheckIfValidEvenIfExpired(jwtToken);
        if (pair.getLeft()) {
            log.info("Jwt Token is not valid, going for refresh !!!");
        }

        AuthVerifyOtpResponse response = new AuthVerifyOtpResponse();
        UUID sessionId = (UUID) pair.getRight();
        LoginUserDO loginUserDO = loginUserRepoService.getAppUserById(sessionId);
        if (loginUserDO == null || loginUserDO.getStatus() != LoginUserStatus.OTP_VERIFIED
                || (hardCheckDeviceId && deviceID.equals(loginUserDO.getDid()))) {
            throw new ChatAppException("invalid auth token", ErrorCodes.INVALID_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtUtil.generateToken(sessionId.toString(), jwtTokenExpiryInMillis);
        response.setSessionId(loginUserDO.getSessionId());
        response.setToken(newAccessToken);
        response.setMessage("refreshed token successfully!");
        return response;
    }
}
