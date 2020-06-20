package work.eanson.service.cheep;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.pojo.Trick;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.service.rule.jq.JQ14TrickAIAnalyzer;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;

/**
 * @author eanson
 * @create 2020-03-26 20:49
 * bug  每次招法是记录之前的坐标 但是招法是记录这一步的 所以把最大值减一 每次获取下一次的坐标
 */
@Service("get_one_step")
public class GetOneStepServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;

    @Input(required = {"cheep_id", "step"})
    @Override
    public void service(Context context) throws Exception {
        String cheepId = String.valueOf(context.get("cheep_id"));
        Integer step = (Integer) context.get("step");
        try (Jedis redis = jedisPool.getResource()) {
            String key = "record_" + cheepId;
            if (!redis.exists(key)) {
                context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
                return;
            }
            List<String> lrange = redis.lrange(key, step, step + 1);
            ObjectMapper objectMapper = new ObjectMapper();
            Trick trick = objectMapper.readValue(lrange.get(0), Trick.class);
            //解决坐标
            if (lrange.size() > 1) {
                Trick trick2 = objectMapper.readValue(lrange.get(1), Trick.class);
                trick.setBefore(trick2.getBefore());
            }
            //最后一个坐标 手动计算坐标
            else {
                if (!trick.getIsFalse()) {
                    new JQ14TrickAIAnalyzer().analyze(trick);
                }
            }
            context.setResult(Result.success(trick));
            context.put("msg", "回放对局");
        }
    }
}
