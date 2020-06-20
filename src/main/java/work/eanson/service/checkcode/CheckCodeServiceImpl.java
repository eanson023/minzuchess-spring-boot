package work.eanson.service.checkcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * 验证码检查
 *
 * @author eanson
 */
@Service("check_code")
public class CheckCodeServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;

    @Input(required = {"telephone", "code"})
    @Override
    public void service(Context context) {
        String telephone = context.get("telephone") + "";
        String code = context.get("code") + "";
        context.clear();
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "code_" + telephone;
            //不存在验证信息 过期 或输入错误
            if (!jedis.exists(key)) {
                context.setResult(Result.fail(MsgCenter.ERROR_PHONE_EXIPRE));
                return;
            }
            String jtel = jedis.hget(key, "telephone");
            String jcode = jedis.hget(key, "random");
            if (!telephone.equals(jtel) || !code.equals(jcode)) {
                context.setResult(Result.fail(MsgCenter.ERROR_CHECK));
                return;
            }
            //验证完毕删除键
            jedis.del(key);
            context.setResult(Result.success());
        }
    }
}
