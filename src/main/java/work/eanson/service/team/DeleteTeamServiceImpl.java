package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * 先检测是不是队长
 *
 * @author eanson
 */
@Service("team_delete")
public class DeleteTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private TeamDao teamDao;

    @Transactional(rollbackFor = Exception.class)
    @Input(required = {"teamId"})
    @Override
    public void service(Context context) throws Exception {
        String teamId = String.valueOf(context.get("teamId"));
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        Boolean aBoolean = teamUserDao.checkIsLeader(teamId, telephone);
        if (aBoolean == null) {
            return;
        }
        if (!aBoolean) {
            return;
        }
        teamUserDao.deleteTeamUserByTeamId(teamId);
        teamDao.deleteByPrimaryKey(teamId);
        context.put("msg","删除队伍");
    }
}
