package work.eanson.service.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.Result;

/**
 * @author eanson
 */
@Service("get_global_status")
public class GetGlobalStatusServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private ChessInfoDao chessInfoDao;

    @Input(required = "code")
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        String s = chessInfoDao.selectPosByPrimaryKey(code);
//        开平方获取宽度
        int width = (int) Math.sqrt(s.length());
        s = s.substring(0, s.length() - width);
        context.setResult(Result.success(s));
    }
}
