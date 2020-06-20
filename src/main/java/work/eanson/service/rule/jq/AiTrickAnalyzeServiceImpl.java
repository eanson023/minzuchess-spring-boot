package work.eanson.service.rule.jq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.controller.websocket.ChessBoardInfoEndPoint;
import work.eanson.controller.websocket.ChessLogEndPoint;
import work.eanson.dao.TrickDao;
import work.eanson.handler.SendChessMessageHandler;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.Trick;
import work.eanson.pojo.extend.ChessInfoExtend;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.service.rule.ChessAIAnalyzer;
import work.eanson.util.ThreadLocalHolder;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;
import java.util.UUID;

/**
 * 1.检查棋盘是不是公共棋盘
 * ai招法
 * 完成后发送棋盘和日志
 *
 * @author eanson
 */
@Service("ai_trick")
public class AiTrickAnalyzeServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;

    @Autowired
    private SendChessMessageHandler sendChessMessageHandler;

    @Autowired
    private ThreadLocalHolder<String> codeHolder;

    @Input(required = {"trick", "password", "code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String password = context.get("password") + "";
        Trick trick = (Trick) context.get("trick");
        context.clear();
        String cbCode = trick.getCbCode();
        int i1 = chessInfoDao.selectIsExistByPrimaryKey(cbCode);
        if (i1 == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        //检查棋盘是不是公共棋盘
        Boolean isPublic = chessInfoDao.selectIsPublicByPrimaryKey(cbCode);
        ChessInfoExtend chessInfoExtend = chessInfoDao.selectUserLoginByPrimaryKey(cbCode);
        if (!isPublic) {
            //查询 user_login和chessInfo表
            String dbPassword = chessInfoExtend.getUserLogin().getPassword();
            String username = chessInfoExtend.getUserLogin().getUsername();
            String jiamiPwd = new Md5Hash(password, username).toHex();
            //验证密码
            if (!jiamiPwd.equals(dbPassword)) {
                context.setResult(Result.fail(MsgCenter.ERROR_PASSWORD));
                return;
            }
        }
        String before;
        //不是后台添加的棋盘码
        if (chessInfoExtend != null) {
            before = chessInfoExtend.getPos();
        } else {
            //后台添加的棋盘码 没有用户名
            before = chessInfoDao.selectPosByPrimaryKey(cbCode);
        }
        //设置之前的pos
        trick.setBefore(before);
        ChessAIAnalyzer analyzer = new JQ14TrickAIAnalyzer();
        //分析
        analyzer.analyze(trick);
        //AI
        trick.setType((byte) 1);
        ChessInfo chessInfo = new ChessInfo();
        chessInfo.setCode(cbCode);
        //发送消息给用户
        try {
            codeHolder.set(cbCode);
            if (trick.getIsFalse()) {
                chessInfo.setClock("--:" + System.currentTimeMillis());
                context.setResult(Result.fail(trick.getMessage()));
            } else {
                //提子 重置棋钟
                if (trick.getStatus() == (byte) 4) {
                    chessInfo.setClock("0z:" + System.currentTimeMillis());
                }
                //普通
                else {
                    chessInfo.setClock("0" + trick.getColor() + ":" + System.currentTimeMillis());
                }
                String newPos = trick.getBefore();
                chessInfo.setPos(newPos);

                sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.usingClients.values(), newPos);
                sendChessMessageHandler.broadcast(ChessBoardInfoEndPoint.watchingClients.values(), newPos);
                context.setResult(Result.success());
            }
            //设置主键
            String key = UUID.randomUUID().toString().replace("-", "");
            trick.setLogId(key);
            //设置回来
            trick.setBefore(before);
            //插入新日志
            trickDao.insertSelective(trick);
            //更新棋盘
            chessInfoDao.updateByPrimaryKeySelective(chessInfo);
            //发送日志到日志页
            int i2 = trickDao.selectCountByForeignKey(cbCode);
            List<TrickExtend> trickExtends = trickDao.selectInfoLimit(cbCode, i2 - 1, i2);
            String logMsg = new ObjectMapper().writeValueAsString(trickExtends);
            sendChessMessageHandler.broadcast(ChessLogEndPoint.clients.values(), logMsg);

        } finally {
            codeHolder.remove();
        }
    }
}
