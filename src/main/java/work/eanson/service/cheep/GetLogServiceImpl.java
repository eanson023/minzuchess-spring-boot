package work.eanson.service.cheep;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.pojo.extend.ChessInfoExtend2;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;

/**
 * 查询 日志
 *
 * @author eanson
 */
@Service("get_log")
public class GetLogServiceImpl extends BaseService implements GlobalService{

    @Autowired
    private TrickDao trickDao;

    public static final Logger logger = LoggerFactory.getLogger(GetLogServiceImpl.class);

    @Input(required = {"code"}, optional = "page")
    @Override
    public void service(Context context) throws Exception {
        String code = String.valueOf(context.get("code"));
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        int i1 = chessInfoDao.selectChessInfoIsExistByPrimaryKey(code);
        if (i1 == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        int i = chessInfoDao.selectCbCodeIsBeLongToOneUser(code, telephone);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_NOT_BELONG));
            return;
        }
        int page = 1;
        Integer fpage = (Integer) context.get("page");
        if (fpage != null) {
            page = fpage;
        }
        PageHelper.startPage(page, 20);
        List<TrickExtend> trickExtends = trickDao.selectTrickInfoOrderTime(code);
        PageInfo<TrickExtend> pageInfo = new PageInfo<>(trickExtends);
        List<ChessInfoExtend2> chessInfoExtend2s = chessInfoDao.selectCodeAndNameByUserId(telephone);
        context.put("chess_info", chessInfoExtend2s);
        context.setResult(Result.success(pageInfo));
    }
}
