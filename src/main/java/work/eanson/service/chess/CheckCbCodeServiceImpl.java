package work.eanson.service.chess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * @author eanson
 */
@Service("check_cb_code")
public class CheckCbCodeServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private ChessInfoDao chessInfoDao;

    @Input(required = "code")
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        int i = chessInfoDao.selectIsExistByPrimaryKey(code);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
        } else {
            //避免 拦截器空指针
            context.put("code", code);
            context.setResult(Result.success());
        }
    }
}
