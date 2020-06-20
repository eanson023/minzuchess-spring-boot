package work.eanson.service.chess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * @author eanson
 */
@Service("check_cb_alias")
public class CheckAliasServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private ChessInfoDao chessInfoDao;

    @Input(required = "alias")
    @Override
    public void service(Context context) throws Exception {
        String alias = context.get("alias") + "";
        context.clear();
        String code = chessInfoDao.selectCodeByAlias(alias);
        if (code == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        context.put("code", code);
        context.setResult(Result.success());
    }
}
