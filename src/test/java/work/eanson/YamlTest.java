package work.eanson;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@SpringBootTest
@PropertySource(value = "classpath:application.yml", factory = PropertySourceFactory.class)
public class YamlTest {

    private String host;

    @Test
    void contextLoads() {
        System.out.println(host);
    }
}
