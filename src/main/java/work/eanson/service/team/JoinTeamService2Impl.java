package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.util.Date;

/**
 * @author eanson
 */
@Service("join_team2")
public class JoinTeamService2Impl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private TeamDao teamDao;
    @Autowired
    private JedisPool jedisPool;


    @Input(required = {"team_id"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String teamId = context.remove("team_id") + "";
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        //判断是否是自己已经加入的队伍
        int i = teamDao.selectUserIsOneTeam(teamId, telephone);
        if (i != 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_TEAM_ALREADY_TEAMMATES));
            return;
        }
        //加入中间表中
        TeamUser teamUser = new TeamUser();
        teamUser.setIsJoin(false);
        teamUser.setTeamId(teamId);
        teamUser.setUserId(telephone);
        teamUser.setJoinTime(new Date());
        teamUserDao.insertSelective(teamUser);
        context.setResult(Result.success());

        //更新消息  找到该队队长手机号 然后更新状态

        try (Jedis resource = jedisPool.getResource();) {
            telephone = teamUserDao.selectLeaderByTeamId(teamId);
            String prefixKey = "shiro:cache:work.eanson.service.realm.LoginRealm.authorizationCache:" + telephone;
            if (resource.exists(prefixKey)) {
                resource.set("update_message_" + telephone, "yes");
            }
        }
        context.put("msg", "加入队伍");
    }
}
