package work.eanson.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * @author eanson
 */
@Service("get_example")
public class GetExampleTeamNameServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;

    @Input(required = "ex")
    @Override
    public void service(Context context) throws Exception {
        String token = context.get("ex") + "";
        try (Jedis resource = jedisPool.getResource()) {
            String s = resource.get(token);
            context.put("example_team_name", s);
        }
    }
}
