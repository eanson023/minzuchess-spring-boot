package work.eanson.service.chess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.controller.websocket.ChessBoardInfoEndPoint;
import work.eanson.controller.websocket.ChessLogEndPoint;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.TrickDao;
import work.eanson.handler.SendChessMessageHandler;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.Trick;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.CheckCbCodeRelation;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.ThreadLocalHolder;

import java.util.List;
import java.util.UUID;

/**
 * 重置棋盘
 * //是否是公共棋盘
 * 是的话清空 不是的话判断是否属于用户
 *
 * @author eanson
 */
@Service("reset_chess")
public class ResetChessServiceImpl extends BaseService implements GlobalService {

    @Autowired
    private ChessInfoDao chessInfoDao;

    @Autowired
    private TrickDao trickDao;

    @Autowired
    private ThreadLocalHolder<String> codeHolder;

    @Autowired
    private SendChessMessageHandler sendChessMessageHandler;

    @CheckCbCodeRelation
    @Input(required = {"code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String iniPos = chessInfoDao.selectInitPosByPrimaryKeyJoinCategory(code);
        String before = chessInfoDao.selectPosByPrimaryKey(code);
        Trick trick = new Trick();
        trick.setBefore(before);
        trick.setMessage("清空棋盘");
        trick.setIsFalse(true);
        trick.setType((byte) 2);
        trick.setStatus((byte) 5);
        String key = UUID.randomUUID().toString().replace("-", "");
        trick.setLogId(key);
        trick.setColor("0");
        trick.setCbCode(code);
        trick.setTrick("clear");
        ChessInfo chessInfo = new ChessInfo();
        chessInfo.setCode(code);
        chessInfo.setPos(iniPos);
        chessInfo.setClock("0Z:" + System.currentTimeMillis());
        chessInfoDao.updateByPrimaryKeySelective(chessInfo);
        trickDao.insertSelective(trick);

        //发送消息
        codeHolder.set(code);
        List<TrickExtend> trickExtends = trickDao.selectByPrimaryKey2One(key);
        String logMsg = new ObjectMapper().writeValueAsString(trickExtends);

        sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.usingClients.values(), iniPos);
        sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.watchingClients.values(), iniPos);
        sendChessMessageHandler.broadcast(ChessLogEndPoint.clients.values(), logMsg);

        codeHolder.remove();
        context.put("msg", "重置棋盘");
    }
}
