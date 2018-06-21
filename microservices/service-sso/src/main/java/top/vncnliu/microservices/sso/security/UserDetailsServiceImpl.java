package top.vncnliu.microservices.sso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import top.vncnliu.microservices.sso.service.UsersService;

/**
 * User: liuyq
 * Date: 2018/4/19
 * Description:
 */
@Component("UserDetailsServiceImplSelf")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersService.findUserByLoginName(username);
    }
}
