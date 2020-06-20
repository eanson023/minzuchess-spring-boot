package work.eanson.service.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.TeamPage;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;

import java.util.ArrayList;
import java.util.List;


/**
 * 查询所有队伍的信息
 *
 * @author eanson
 */
@Service("all_team")
public class AllTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;

    @Override
    public void service(Context context) throws Exception {
        List<TeamUser> teamUsers = teamUserDao.selectAllTeamInfo();
        List<TeamPage> teamPages = new ArrayList<>();
        for (TeamUser teamUser : teamUsers) {
            TeamPage teamPage = new TeamPage();
            teamPage.setTeamUser(teamUser);
            int i = teamUserDao.selectJoinedCountByTeamId(teamUser.getTeamId());
            teamPage.setCounts(i);
            teamPages.add(teamPage);
        }
        context.put("teamPages", teamPages);
    }
}
