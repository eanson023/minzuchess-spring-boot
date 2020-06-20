package work.eanson.service.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.TeamDao;
import work.eanson.pojo.Team;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.List;

/**
 * 模糊查询
 *
 * @author eanson
 */
@Service("team_find_like")
public class FindLikeTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;

    @Input(required = "team_or_name")
    @Override
    public void service(Context context) throws Exception {
        String tIdorName = context.get("team_or_name") + "";
        //如果是数字的话至少6位才查询
        if (isNumber(tIdorName)) {
            if (tIdorName.length() < 6) {
                context.setResult(Result.fail(MsgCenter.ERROR_NO_TEAM));
                return;
            }
        }
        List<Team> teams = teamDao.selectTeamLike(tIdorName);
        context.setResult(Result.success(teams));
        context.put("msg", "查找队伍");
    }
}
