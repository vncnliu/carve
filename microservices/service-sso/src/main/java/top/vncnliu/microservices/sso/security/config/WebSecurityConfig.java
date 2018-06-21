package top.vncnliu.microservices.sso.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.vncnliu.microservices.sso.security.JWTOAuthFilter;
import top.vncnliu.microservices.sso.service.AppService;
import top.vncnliu.microservices.sso.service.UsersService;

/**
 * User: liuyq
 * Date: 2018/4/13
 * Description:
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsersService usersService;

    @Autowired
    private AppService appService;

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers().anyRequest()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/*","/error/**").permitAll()
                .and().addFilterBefore(new JWTOAuthFilter("/oauth/**", authenticationManager(),usersService,appService),
                UsernamePasswordAuthenticationFilter.class);
    }

}
