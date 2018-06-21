package top.vncnliu.microservices.sso.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: liuyq
 * Date: 2018/4/19
 * Description:
 */
@Entity
@Data
public class App {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;
}
