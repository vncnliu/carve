package top.vncnliu.microservices.sso.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * User: liuyq
 * Date: 2018/6/20
 * Description:
 */
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private Date time_create = new Date();

    private Date  time_modified = new Date();
}
