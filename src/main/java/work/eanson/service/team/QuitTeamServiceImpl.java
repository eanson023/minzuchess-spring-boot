package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * 退出队伍
 *
 * @author eanson
 */
@Service("team_quit")
public class QuitTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;

    @Input(required = {"teamId"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String teamId = String.valueOf(context.get("teamId"));
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        teamUserDao.deleteByTeamIdAndUserId(teamId, telephone);
        context.put("msg", "退出队伍");
    }
}
