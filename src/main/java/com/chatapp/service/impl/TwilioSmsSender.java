package com.chatapp.service.impl;

import com.chatapp.service.SmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioSmsSender implements SmsSender {

    @Value("${chatapp.twilio.phone_number}")
    private String twilioNumber;

    @Value("${chatapp.twilio.account_sid}")
    private String accountSid;

    @Value("${chatapp.twilio.auth_token}")
    private String authToken;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public String sendOtp(@NonNull String toPhoneNumber, @NonNull String otp) {
        Message message = Message.creator(
                        new PhoneNumber("+91" + toPhoneNumber),  // TO
                        new PhoneNumber(twilioNumber),  // FROM (Twilio)
                        "Your ChatApp verification Code for Login is: " + otp)
                .create();

        return message.getSid();
    }
}
