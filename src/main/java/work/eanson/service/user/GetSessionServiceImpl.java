package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.back.Message;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;

import java.util.List;


/**
 * 获取用户session
 * 为了避免数据不及时 所以每次都要检测有没有要加入的
 *
 * @author eanson
 */
@Service("get_session")
public class GetSessionServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private TeamUserDao teamUserDao;

    private static final Logger logger = LoggerFactory.getLogger(GetSessionServiceImpl.class);

    @Override
    public void service(Context context) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        UserSession userSession = (UserSession) subject.getPrincipal();
        if (userSession == null) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String telephone = userSession.getTelephone();
            String key = "update_message_" + telephone;
            if (jedis.exists(key)) {
                logger.info("更新消息");
                List<Message> messages = teamUserDao.selectWantJoinTeam(telephone);
                userSession.setMessages(messages);
                int size = messages.size();
                userSession.setMsgCount(size);
                userSession.setMessages(messages);
                updateNewSession(userSession);
                jedis.del(key);
            }
        }
        context.put("user", userSession);
    }
}
