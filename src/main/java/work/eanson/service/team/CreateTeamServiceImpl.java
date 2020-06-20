package work.eanson.service.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.pojo.security.SecurityInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.IdWorker;
import work.eanson.util.RedisToken;

import java.util.Date;

/**
 * 创建队伍
 * 1.解密token
 * 2.加入队伍表，更新usrInfo表
 * 3.查询user_login表返回登录信息
 *
 * @author eanson
 */
@Service("create_team")
public class CreateTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;
    @Autowired
    private TeamUserDao teamUserDao;

    @Autowired
    private RedisToken redisToken;
    @Autowired
    private IdWorker idWorker;

    @Input(required = {"userInfo", "team"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        UserInfo userInfo = (UserInfo) context.get("userInfo");
        Team team = (Team) context.get("team");
        String reqToken = context.get("req_token") + "";
        context.clear();
        SecurityInfo securityInfo = redisToken.getSecurityInfo(reqToken);
        String telephone = redisToken.getSessionTelephone(reqToken);
        redisToken.delToken(securityInfo);
        userInfo.setTelephone(telephone);
        String teamKey = idWorker.nextId() + "";
        int i = team.getSchoolId();
        userInfo.setStat((byte) 3);
        if (i == -1) {
            team.setSchoolId(null);
            userInfo.setStat((byte) 2);
        }
        //设置主键
        team.setTeamId(teamKey);
        team.setCreateTime(new Date());
        //加入
        teamDao.insert(team);
        //加入中间表
        TeamUser teamUser = new TeamUser();
        //队长
        teamUser.setIsLeader(true);
        teamUser.setUserId(telephone);
        teamUser.setTeamId(teamKey);
        //自己是队长 所以已加入
        teamUser.setIsJoin(true);
        teamUser.setJoinTime(new Date());
        teamUserDao.insertSelective(teamUser);

        ////更新用户是否完成注册状态


        UserLogin userLogin = new UserLogin();
        userLogin.setIsRegHalf(false);
        userLogin.setTelephone(telephone);
        userLoginDao.updateByPrimaryKeySelective(userLogin);

        //回写数据
        UserLogin userLogin2 = userLoginDao.selectByPrimaryKey(telephone);
        context.put("userLogin", userLogin2);
    }
}
