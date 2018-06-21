package top.vncnliu.microservices.sso.service;

import org.springframework.stereotype.Service;
import top.vncnliu.microservices.sso.Entity.App;
import top.vncnliu.microservices.sso.repository.AppRepository;

/**
 * User: liuyq
 * Date: 2018/4/18
 * Description:
 */
@Service
public class AppService extends AbsService<App,Integer> {

    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        super(appRepository);
        this.appRepository = appRepository;
    }

    public App findByName(String name) {
        return appRepository.getOne(1);
    }
    public App findByID(Integer id) {
        return appRepository.getOne(id);
    }
}
