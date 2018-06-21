package top.vncnliu.microservices.sso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import top.vncnliu.microservices.sso.Entity.Users;
import top.vncnliu.microservices.sso.repository.UserRepository;

/**
 * User: liuyq
 * Date: 2018/4/18
 * Description:
 */
@Service
public class UsersService extends AbsService<Users,Integer> {

    private final UserRepository userRepository;

    @Autowired
    public UsersService(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    public Users findUserByLoginName(String name){
        return userRepository.findOne(Example.of(new Users().setLogin_name(name))).orElse(null);
    }
}
