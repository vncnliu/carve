import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.vncnliu.microservices.sso.ApplicationSSO;
import top.vncnliu.microservices.sso.Entity.App;
import top.vncnliu.microservices.sso.Entity.Users;
import top.vncnliu.microservices.sso.repository.AppRepository;
import top.vncnliu.microservices.sso.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

/**
 * User: liuyq
 * Date: 2018/4/18
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApplicationSSO.class)
public class Test {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppRepository appRepository;

    @org.junit.Test
    public void main(){
        Users users = new Users();
        users.setId(1);
        userRepository.save(users);
    }

    @org.junit.Test
    public void testFindA(){
        App app = appRepository.findById(1).get();
        System.out.println(app);
    }

    @org.junit.Test
    public void testJackson(){
        final ObjectMapper mapper = new ObjectMapper();
        try {
            Map map = mapper.readValue("{\"a\":1,\"b\":2}", Map.class);
            System.out.println(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
