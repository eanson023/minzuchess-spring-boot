package work.eanson.service.chess;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * 公开棋盘码 或者取消公开
 *
 * @author eanson
 */
@Service("public_or_cancel_chess_board")
public class UpdateIsPublicServiceImpl extends BaseService implements GlobalService {

    @Input(required = {"code"})
    @Override
    public void service(Context context) throws Exception {
        String code = context.get("code") + "";
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        chessInfoDao.updateIsPublicByPrimaryKeyAndForeignKey(code, telephone);
        context.put("msg", "更新棋盘状态");
    }
}
