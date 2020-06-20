package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.IdWorker;
import work.eanson.util.Result;

import java.util.Date;

/**
 * @author eanson
 */
@Service("create_team2")
public class CreateTeamService2Impl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private IdWorker idWorker;

    @Transactional(rollbackFor = Exception.class)
    @Input(required = {"team"})
    @Override
    public void service(Context context) throws Exception {
        Team team = (Team) context.get("team");
        context.clear();
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        String teamId = idWorker.nextId() + "";
        team.setTeamId(teamId);
        team.setCreateTime(new Date());
        teamDao.insertSelective(team);
        TeamUser teamUser = new TeamUser();
        teamUser.setUserId(telephone);
        teamUser.setTeamId(teamId);
        teamUser.setIsJoin(true);
        teamUser.setIsLeader(true);
        teamUser.setJoinTime(new Date());
        teamUserDao.insertSelective(teamUser);
        context.setResult(Result.success());
        context.put("msg", "创建新队伍");
    }
}
