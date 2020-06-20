package work.eanson.service.cheep;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.pojo.back.page.DataTablesBack;
import work.eanson.pojo.back.page.DataTablesIn;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.Date;
import java.util.List;

/**
 * 精选
 *
 * @author eanson
 */
@Service("get_log_from_to")
public class GetLogFromToServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;

    @Input(required = {"from", "to", "code", "dti"})
    @Override
    public void service(Context context) throws Exception {
        String code = String.valueOf(context.get("code"));
        Date from = (Date) context.get("from");
        Date to = (Date) context.get("to");
        DataTablesIn dti = (DataTablesIn) context.get("dti");
        int start = dti.getStart();
        int length = dti.getLength();
        int page = start / length + 1;
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
        //如果比到大 交换
        if (from.getTime() > to.getTime()) {
            Date tmp;
            tmp = from;
            from = to;
            to = tmp;
        }
        PageHelper.startPage(page, length);
        List<TrickExtend> trickExtends = trickDao.selectTrickInfoOrderTimeLimitTime(from, to, code);
        DataTablesBack<TrickExtend> dtb = new DataTablesBack<>(trickExtends);
        context.setResult(Result.success(dtb));
        context.put("msg", "筛选查询棋盘日志信息");
    }
}
