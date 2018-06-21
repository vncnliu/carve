package me.vncnliu.microservices.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.util.Arrays;

/**
 * User: liuyq
 * Date: 2018/4/18
 * Description:
 */
@Configuration
public class OAuth2ServerConfig {


    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId("quote").stateless(true);
            resources.tokenExtractor(new JWTTokenExtractor());
            resources.authenticationManager(new ProviderManager(Arrays.asList(new CustomAuthenticationProvider())));
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated();
        }
    }
}
