package com.chatapp.config;

import com.chatapp.ChatAppUtil;
import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.service.LoginUserRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private LoginUserRepoService loginUserRepoService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // .antMatchers(...).permitAll()	Allows access — skips auth check, not the filter
        String uri = request.getRequestURI();
        return uri.endsWith(".html") || uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 🥠 Get JWT from Cookie
        String jwt = ChatAppUtil.extractTokenFromCookies(request.getCookies());
        String sessionId = null;
        log.info("JwtFilter invoked for URI: " + request.getRequestURI());
        try {
            sessionId = jwtUtil.extractSubjectForFilter(jwt);
            if (!loginUserRepoService.existsBySessionIdAndStatus(UUID.fromString(sessionId), LoginUserStatus.OTP_VERIFIED))
                sessionId = null;
        } catch (Exception e) {
            log.error("Error : " + e.getMessage(), e);
            // ignore error
        }

        if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication auth = authManager.authenticate(new UuidAuthenticationToken(UUID.fromString(sessionId)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
