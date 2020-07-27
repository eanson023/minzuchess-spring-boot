package work.eanson.service.chess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.service.rule.jq.JQ14TrickAIAnalyzer;
import work.eanson.util.Context;
import work.eanson.util.Result;

import java.util.concurrent.TimeUnit;


/**
 * @author eanson
 * <p>
 * 获取或设置棋盘位置
 */
@Service("get_set_chess")
public class GetSetChessServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;
    private static final Logger logger = LoggerFactory.getLogger(GetSetChessServiceImpl.class);

    /**
     * @param context 装载数据的工具
     * @throws Exception
     */
    @Input(required = "code", optional = "pos")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String pos = context.get("pos") + "";
        ChessInfo chessInfo = chessInfoDao.selectByPrimaryKey(code);
//        设置信息
        String nullS = "null";
//        如果 pos补位空 且 与之前的不相等 则更新pos
        if (!nullS.equals(pos) && !pos.equals(chessInfo.getPos())) {
            ChessInfo chessInfo1 = new ChessInfo();
            chessInfo1.setCode(code);
            chessInfo1.setPos(pos);
            try {
                chessInfoDao.updateByPrimaryKeySelective(chessInfo1);
            } catch (DeadlockLoserDataAccessException e) {
                logger.error("提子时,行死锁");
//                如果出现死锁 则沉睡后重新更新
                TimeUnit.MILLISECONDS.sleep(200);
                JQ14TrickAIAnalyzer analyzer = new JQ14TrickAIAnalyzer();
                if (analyzer.isCbFull(pos)) {
                    pos = analyzer.getAfterFcCenterChess(pos);
                }
//                清空缓存
                try (Jedis jedis = jedisPool.getResource()) {
                    String key = "qipu_zset_" + code;
                    jedis.del(key);
                    jedis.zadd(key, 0, pos);
                }
                context.put("pos", pos);
                service(context);
                return;
            }
            context.setResult(Result.success(chessInfo1.getPos()));
            return;
        }
        context.setResult(Result.success(chessInfo.getPos()));
    }
}
