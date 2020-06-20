package work.eanson.service.chess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

import java.util.Set;


/**
 * 更新一位坐标 redis缓存
 * <p>
 * 需要解决回路问题
 * 1.判断size是否大于2
 * 2.判断坐标是否与第一次相同
 *
 * @author eanson
 */
@Service("update_cache")
public class UpdateCacheServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;

    @Input(required = {"code", "pos"})
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String after = context.get("pos") + "";
        ChessInfo chessInfo = chessInfoDao.selectByPrimaryKey(code);
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "qipu_zset_" + code;
            Long zcount = jedis.zcount(key, 0, 300);
            //计算棋盘宽度
            String before = chessInfo.getPos();
            //解决回路问题
            if (zcount > 1) {
                Set<String> zrange = jedis.zrange(key, 0, 0);
                //让想等的首尾不相等
                for (String s : zrange) {
                    if (s.equals(after)) {
                        //删除原来的
                        jedis.zrem(key, s);
                        s += "-";
                        //添加变换的到原来的位置
                        jedis.zadd(key, 0, s);
                    }
                }
            }
            //1.之前的信息
            jedis.zadd(key, zcount++, before);
            //2.之后的信息
            jedis.zadd(key, zcount, after);
        }
    }
}
