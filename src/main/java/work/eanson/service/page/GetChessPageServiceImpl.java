package work.eanson.service.page;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.CategoryDao;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * @author eanson
 */
@Service("get_page")
public class GetChessPageServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ChessInfoDao chessInfoDao;

    private static final Logger logger = LoggerFactory.getLogger(GetChessPageServiceImpl.class);

    @Input(optional = {"code", "alias"})
    @Override
    public void service(Context context) throws Exception {
        Object codeObj = context.get("code");
        Object aliasObj = context.get("alias");
        context.clear();
        boolean isAlias = false;
        String code = null;
        if (codeObj != null) {
            code = String.valueOf(codeObj);
        } else if (aliasObj != null) {
            String alias = String.valueOf(aliasObj);
            code = chessInfoDao.selectCodeByAlias(alias);
            isAlias = true;
        }
        /**
         * 没有该参数
         */
        if (code == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
        int i1 = chessInfoDao.selectIsExistByPrimaryKey(code);
        /**
         * 没有该棋盘码
         */
        if (i1 == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        /**
         * 是否是公共
         */
        Boolean aBoolean = chessInfoDao.selectIsPublicByPrimaryKey(code);
        String html = categoryDao.selectHtmlByCode(code);
        //设置html 用于转发
        context.setResult(Result.success(html));
        if (isAlias) {
            return;
        }
        //公共棋盘
        if (aBoolean) {
            return;
        }
        Subject subject = SecurityUtils.getSubject();
        UserSession userSession = (UserSession) subject.getPrincipal();
        /**
         * 如果说该用户没有登录 则需要跳转到主页
         */
        if (userSession == null) {
            context.setResult(Result.fail(MsgCenter.USER_NOT_LOGIN));
            return;
        }
        String telephone = userSession.getTelephone();
        /**
         * 检测棋盘是否属于该用户
         */
        int i = chessInfoDao.selectCbCodeIsBeLongToOneUser(code, telephone);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_NOT_BELONG));
        }
    }
}
