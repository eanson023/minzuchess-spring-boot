package work.eanson.service.chess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.service.rule.jq.JQ14TrickAIAnalyzer;
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

    private static final Logger logger = LoggerFactory.getLogger(UpdateCacheServiceImpl.class);

    /**
     * 此方法先于{@link SetClockServiceImpl}执行 当检测到提子时需要更新棋盘数据
     *
     * @param context 装载数据的工具
     * @throws Exception
     */
    @Input(required = {"code", "pos"})
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String after = context.get("pos") + "";
        ChessInfo chessInfo = chessInfoDao.selectByPrimaryKey(code);
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "qipu_zset_" + code;
            JQ14TrickAIAnalyzer jq14TrickAiAnalyzer = new JQ14TrickAIAnalyzer();
//            如果满了 就提子 更新缓存
            if (jq14TrickAiAnalyzer.isCbFull(chessInfo.getPos())) {
                chessInfo.setPos(jq14TrickAiAnalyzer.getAfterFcCenterChess(chessInfo.getPos()));
                ChessInfo chessInfo2 = new ChessInfo();
                chessInfo2.setPos(chessInfo.getPos());
                chessInfo2.setCode(code);
                logger.error("提子后新信息:" + chessInfo.getPos());
//                保存新信息
                chessInfoDao.updateByPrimaryKeySelective(chessInfo2);
            }
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
