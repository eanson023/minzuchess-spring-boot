package work.eanson.service.chess;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;


/**
 * 1.检查是棋盘码 还是棋盘编码
 * 2.棋盘码 是否登录
 * 3.编码
 * 4.组装url
 *
 * @author eanson
 */
@Service("go_chess")
public class GoChessServiceImpl extends BaseService implements GlobalService {

    @Autowired
    private ChessInfoDao chessInfoDao;

    private static final Logger logger = LoggerFactory.getLogger(GoChessServiceImpl.class);

    @Input(required = {"code_or_alias"})
    @Override
    public void service(Context context) throws Exception {
        String codeOrAlias = context.get("code_or_alias") + "";
        boolean isCode = true;
        if (isNumber(codeOrAlias)) {
            isCode = false;
        }
        String url;
        if (isCode) {
            //检查是不是公开棋盘
            Boolean aBoolean = chessInfoDao.selectIsPublicByPrimaryKey(codeOrAlias);
            if (aBoolean == null) {
                context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
                return;
            }
            if (aBoolean) {
                url = "/chess?" + "n_t=" + System.currentTimeMillis() + "&ex=" + getUUID() + "&ex2=" + getUUID() + "&code=" + codeOrAlias;
                context.setResult(Result.success(url));
                context.put("is_code", true);
                return;
            }
            //是否登录
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                context.setResult(Result.fail(MsgCenter.USER_NOT_LOGIN));
                return;
            }
            UserSession session = (UserSession) subject.getPrincipal();
            //检查该棋盘码是否是该用户的
            String telephone = session.getTelephone();
            logger.info("shiro测试1" + telephone);
            int i = chessInfoDao.selectCbCodeIsBeLongToOneUser(codeOrAlias, telephone);
            if (i == 0) {
                context.setResult(Result.fail(MsgCenter.ERROR_CODE_NOT_BELONG));
                return;
            }
            url = "/chess?" + "n_t=" + System.currentTimeMillis() + "&ex=" + getUUID() + "&ex2=" + getUUID() + "&code=" + codeOrAlias;
        } else {
            url = "/chess/watch?" + "n_t=" + System.currentTimeMillis() + "&ex=" + getUUID() + "&ex2=" + getUUID() + "&alias=" + codeOrAlias;
        }
        context.setResult(Result.success(url));
        context.put("msg", "下棋或者观战");
    }
}
