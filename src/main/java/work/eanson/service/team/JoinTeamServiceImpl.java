package work.eanson.service.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.RedisToken;

import java.util.Date;


/**
 * 加入队伍
 *
 * @author eanson
 */
@Service("join_team")
public class JoinTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private RedisToken redisToken;
    @Autowired
    private JedisPool jedisPool;

    /**
     * 加入队伍 根据req_token找到手机号，然后跟新中间表
     *
     * @param context
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Input(required = {"req_token", "team"})
    @Override
    public void service(Context context) throws Exception {
        String reqToken = context.remove("req_token") + "";
        Team team = (Team) context.remove("team");
        //解密token
        String telephone = redisToken.getSessionTelephone(reqToken);
        redisToken.delToken(reqToken);
        //加入中间表中
        TeamUser teamUser = new TeamUser();
        teamUser.setIsJoin(false);
        teamUser.setTeamId(team.getTeamId());
        teamUser.setUserId(telephone);
        teamUser.setJoinTime(new Date());
        teamUserDao.insertSelective(teamUser);


        ////更新用户是否完成注册状态

        UserLogin userLogin = new UserLogin();
        userLogin.setIsRegHalf(false);
        userLogin.setTelephone(telephone);
        userLoginDao.updateByPrimaryKeySelective(userLogin);
        //查询用户登录表
        UserLogin userLogin2 = userLoginDao.selectByPrimaryKey(telephone);
        context.put("userLogin", userLogin2);

        //更新消息  找到该队队长手机号 然后更新状态
        try (Jedis resource = jedisPool.getResource()) {
            telephone = teamUserDao.selectLeaderByTeamId(team.getTeamId());
            String prefixKey = "shiro:cache:work.eanson.service.realm.LoginRealm.authorizationCache:" + telephone;
            if (resource.exists(prefixKey)) {
                resource.set("update_message_" + telephone, "yes");
            }
        }
    }
}
