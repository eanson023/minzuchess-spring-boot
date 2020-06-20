package work.eanson.service.cheep;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * @author eanson
 */
@Service("get_one_code")
public class GetOneCodeServiceImpl extends BaseService implements GlobalService {

    @Autowired
    private ChessInfoDao chessInfoDao;

    @Override
    public void service(Context context) throws Exception {
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        String code = chessInfoDao.selectOneCbCode(telephone);
        if (code == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        String uri = "/cheep/my_log?code=" + code;
        context.setResult(Result.success(uri));
    }
}
