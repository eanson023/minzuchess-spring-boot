package work.eanson.service.chess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.controller.websocket.ChessBoardInfoEndPoint;
import work.eanson.controller.websocket.ChessLogEndPoint;
import work.eanson.dao.TrickDao;
import work.eanson.handler.SendChessMessageHandler;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.Trick;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.ThreadLocalHolder;

import java.util.List;
import java.util.UUID;

/**
 * 返回至某一步
 *
 * @author eanson
 */
@Service("go_back")
public class GoBackServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;
    @Autowired
    private ThreadLocalHolder<String> codeHolder;

    @Autowired
    private SendChessMessageHandler sendChessMessageHandler;

    @Input(required = {"code", "log_id"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String logId = context.get("log_id") + "";
        Trick trick = trickDao.selectByPrimaryKey(logId);
        if (trick == null) {
            return;
        }
        ChessInfo chessInfo1 = chessInfoDao.selectByPrimaryKey(code);
        if (chessInfo1 == null) {
            return;
        }
        ChessInfo chessInfo = new ChessInfo();
        chessInfo.setPos(trick.getBefore());
        chessInfo.setCode(code);
        String color = trick.getColor();
        String clock;
        String wc = "z";
        String bc = "Z";
        //相反的棋钟
        if (wc.equals(color)) {
            clock = "0Z:" + System.currentTimeMillis();
        } else if (bc.equals(color)) {
            clock = "0z:" + System.currentTimeMillis();
        } else {
            clock = "--:" + System.currentTimeMillis();
        }
        chessInfo.setClock(clock);
        trick.setTrick("back");
        trick.setBefore(chessInfo1.getPos());
        trick.setMessage("返回");
        trick.setExuteTime(null);
        trick.setColor("0");
        trick.setIsFalse(false);
        String key = UUID.randomUUID().toString().replace("-", "");
        trick.setLogId(key);
        trick.setType((byte) 2);
        trick.setStatus((byte) 5);
        trickDao.insertSelective(trick);
        chessInfoDao.updateByPrimaryKeySelective(chessInfo);

        //发送消息
        List<TrickExtend> trickExtends = trickDao.selectByPrimaryKey2One(key);
        String json = new ObjectMapper().writeValueAsString(trickExtends);
        codeHolder.set(code);
        //日志
        sendChessMessageHandler.broadcast(ChessLogEndPoint.clients.values(), json);
        //坐标
        sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.watchingClients.values(), chessInfo.getPos());
        sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.usingClients.values(), chessInfo.getPos());

        codeHolder.remove();
        context.put("msg", "返回至某一步");
    }
}
