package me.vncnliu.microservices.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import top.vncnliu.microservices.utils.TokenAuthenticationService;

import javax.servlet.http.HttpServletRequest;

/**
 * User: liuyq
 * Date: 2018/4/20
 * Description:
 */
public class JWTTokenExtractor implements TokenExtractor {
    @Override
    public Authentication extract(HttpServletRequest request) {
        return TokenAuthenticationService.getAuthentication(request);
    }
}
