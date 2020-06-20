package work.eanson.service.team;


import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.extend.TeamUserExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;

/**
 * @author eanson
 */
@Service("get_team_user")
public class GetTeamUserPageServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private TeamDao teamDao;

    @Input(required = {"team_id", "page_num"}, optional = "size")
    @Override
    public void service(Context context) throws Exception {
        String teamId = String.valueOf(context.get("team_id"));
        int pageNum = (int) context.get("page_num");
        Object sizeObj = context.get("size");
        context.clear();
        int i = teamDao.selectTeamIsExistsByTeamId(teamId);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_FIND_TEAM));
            return;
        }
        int size = 6;
        if (sizeObj != null) {
            size = (int) sizeObj;
        }
        PageHelper.startPage(pageNum, size);
        List<TeamUserExtend> teamUserExtends = teamUserDao.selectOneTeamUserInfo2ByTeamId(teamId);
        context.setResult(Result.success(teamUserExtends));
    }
}
