package work.eanson.service.checkcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.Result;

/**
 * 验证码保存
 *
 * @author eanson
 */
@Service("save_code")
public class SaveCodeServiceImpl extends BaseService  implements GlobalService{
    @Autowired
    private JedisPool jedisPool;

    @Input(required = {"telephone", "random"})
    @Override
    public void service(Context context) {
        try (Jedis jedis = jedisPool.getResource()) {
            String telephone = context.get("telephone") + "";
            String random = context.get("random") + "";
            context.clear();
            String key = "code_" + telephone;
            jedis.hset(key, "telephone", telephone);
            jedis.hset(key, "random", random);
            //5分钟
            jedis.expire(key, 60 * 5);
            context.setResult(Result.success());
        }
    }
}
