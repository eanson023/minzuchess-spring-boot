package work.eanson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.eanson.handler.SendErrorMessageMailHandler;

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
public class CommonTest {
    @Autowired
    SendErrorMessageMailHandler SendErrorMessageMailHandler;

    @Test
    public void mailTest() {
        SendErrorMessageMailHandler.sendMailToAdmin("xixi", "haha");
    }

}
