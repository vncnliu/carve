package top.vncnliu.microservices.sso.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import top.vncnliu.microservices.sso.Entity.App;
import top.vncnliu.microservices.sso.Entity.Users;
import top.vncnliu.microservices.sso.constant.ErrorCode;
import top.vncnliu.microservices.sso.service.AppService;
import top.vncnliu.microservices.sso.service.UsersService;
import top.vncnliu.microservices.utils.TokenAuthenticationService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: liuyq
 * Date: 2018/4/20
 * Description:
 */
public class JWTOAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final UsersService usersService;
    private final AppService appService;

    public JWTOAuthFilter(String url, AuthenticationManager authManager, UsersService usersService, AppService appService) {
        super(new AntPathRequestMatcher(url));
        this.usersService = usersService;
        this.appService = appService;
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException {

        Map map = new ObjectMapper().readValue(req.getInputStream(), Map.class);
        String client = (String) map.get("client");
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        Users user = usersService.findUserByLoginName(username);
        if(user==null){
            throw new UsernameNotFoundException(ErrorCode.UserNotExit.getMsg());
        }
        if(!user.getPassword().equals(password)){
            throw new AuthenticationServiceException(ErrorCode.ErrorPassword.getMsg());
        }
        App app = appService.findById(user.getApp_id());
        if(app==null||!client.equals(app.getName())){
            throw new AuthenticationServiceException(ErrorCode.ErrorClientUser.getMsg());
        }
        // 返回一个验证令牌
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) {
        TokenAuthenticationService.addAuthentication(res, auth.getName());
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String msg = "{\"error\":\"unsuccessful authentication\",\"error_description\":\""+failed.getMessage()+"\"}";
        response.getOutputStream().write(msg.getBytes("UTF-8"));
    }
}
