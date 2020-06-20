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

/**
 * 根据队名或队伍编号查找队伍
 *
 * @author eanson
 */
@Service("find_team")
public class FindTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;

    @Input(required = "team_name_num")
    @Override
    public void service(Context context) throws Exception {
        String teamNameNum = context.get("team_name_num") + "";
        Team team = teamDao.selectByPrimaryKeyOrTeamName(teamNameNum);
        if (team == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_FIND_TEAM));
            return;
        }
        context.setResult(Result.success());
        context.put("team", team);
        context.put("msg", "查找队伍");
    }
}
