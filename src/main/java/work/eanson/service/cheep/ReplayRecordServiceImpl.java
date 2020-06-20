package work.eanson.service.cheep;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.CheepDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.Cheep;
import work.eanson.pojo.Trick;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.Date;
import java.util.List;

/**
 * @author eanson
 * @create 2020-03-26 16:20
 * <p>
 * key:record_+cheepId;
 * <p>
 * 采用链表list数据结构 有序可重复 之后通过下标取值
 */
@Service("replay_record")
public class ReplayRecordServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private CheepDao cheepDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private TrickDao trickDao;

    @Input(required = "cheep_id")
    @Override
    public void service(Context context) throws Exception {
        String cheepId = String.valueOf(context.get("cheep_id"));
        Cheep cheep = cheepDao.selectByPrimaryKey(cheepId);
        if (cheep == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
        context.setResult(Result.success());
        String html = cheepDao.selectHtmlByPrimaryKey(cheepId);
        context.put("html", html + "_replay");
        try (Jedis redis = jedisPool.getResource()) {
            String key = "record_" + cheepId;
            if (redis.exists(key)) {
                Long max = redis.llen(key);
                context.put("max", max - 1);
                return;
            }
            Date from = cheep.getFrom();
            Date to = cheep.getTo();
            String code = cheep.getCode();
            List<Trick> trickList = trickDao.selectPosesOrderTimeLimitTime(from, to, code);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Trick trick : trickList) {
                String s = objectMapper.writeValueAsString(trick);
                redis.rpush(key, s);
            }
            context.put("max", trickList.size() - 1);
        }
    }
}
