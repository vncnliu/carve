package top.vncnliu.microservices.sso.service;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: liuyq
 * Date: 2018/4/19
 * Description:
 */
public abstract class AbsService<T,ID> {

    JpaRepository<T,ID> jpaRepository;

    public AbsService(JpaRepository<T, ID> jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public T findById(ID id){
        return jpaRepository.findById(id).orElse(null);
    }
}
