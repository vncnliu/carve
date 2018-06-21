package top.vncnliu.microservices.sso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.vncnliu.microservices.sso.Entity.Users;

/**
 * User: liuyq
 * Date: 2018/4/18
 * Description:
 */
public interface UserRepository extends JpaRepository<Users,Integer> {
}
