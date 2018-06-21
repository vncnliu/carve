import com.fasterxml.jackson.databind.ObjectMapper;
import me.vncnliu.microservices.web.event.QuoteEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * add description<br>
 * created on 2018/2/13<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class JsonTest {

    @Test
    public void main() throws IOException {
        String json = "{\"code\":\"s711\",\"price\":0.24312090137573927}";
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.readValue(json,QuoteEvent.class));
    }
}
