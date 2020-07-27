package work.eanson.service.chess;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.service.rule.jq.JQ14TrickAIAnalyzer;
import work.eanson.util.Context;
import work.eanson.util.Result;

/**
 * @author eanson
 * <p>
 * 获取或设置棋盘位置
 */
@Service("get_set_chess")
public class GetSetChessServiceImpl extends BaseService implements GlobalService {

    @Input(required = "code", optional = "pos")
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String pos = context.get("pos") + "";
        context.clear();
        ChessInfo chessInfo = chessInfoDao.selectByPrimaryKey(code);
//        if (chessInfo == null) {
//            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
//            return;
//        }
//        设置棋盘 信息
        String nullS = "null";
        if (!nullS.equals(pos)) {
            //        如果棋盘满了 则更新为提子后的坐标
            JQ14TrickAIAnalyzer jq14TrickAiAnalyzer = new JQ14TrickAIAnalyzer();
            if (jq14TrickAiAnalyzer.isCbFull(pos)) {
                pos = jq14TrickAiAnalyzer.getAfterFcCenterChess(pos);
            }
            ChessInfo chessInfo1 = new ChessInfo();
            chessInfo1.setCode(code);
            chessInfo1.setPos(pos);
            chessInfoDao.updateByPrimaryKeySelective(chessInfo1);
            context.setResult(Result.success(chessInfo1.getPos()));
            return;
        }
        context.setResult(Result.success(chessInfo.getPos()));
    }
}
