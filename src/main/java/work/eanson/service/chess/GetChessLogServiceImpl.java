package work.eanson.service.chess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;

/**
 * 获取日志
 *
 * @author eanson
 */
@Service("get_chess_log")
public class GetChessLogServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;
    @Autowired
    private ChessInfoDao chessInfoDao;

    @Input(optional = {"num", "code", "alias"})
    @Override
    public void service(Context context) throws Exception {
        Object codeObj = context.get("code");
        Object aliasObj = context.get("alias");
        Object numObj = context.get("num");
        context.clear();
        String code = null;
        if (codeObj != null) {
            code = String.valueOf(codeObj);
            context.put("is_chess", true);
            context.put("code", code);
        } else if (aliasObj != null) {
            String alias = String.valueOf(aliasObj);
            code = chessInfoDao.selectCodeByAlias(alias);
            context.put("is_chess", false);
            context.put("alias", alias);
        }
        if (code == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
        Integer num;
        if (numObj != null) {
            num = (Integer) numObj;
        } else {
            num = 10;
        }
        int i = trickDao.selectCountByForeignKey(code);
        //10条
        int res = i - num < 0 ? 0 : i - num;
        List<TrickExtend> tricks = trickDao.selectInfoLimit(code, res, i);
        context.setResult(Result.success(tricks));
        context.put("msg", "下棋页获取棋盘历史招数");
    }
}
