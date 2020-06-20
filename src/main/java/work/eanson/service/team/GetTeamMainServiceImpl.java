package work.eanson.service.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.back.TeamMain;
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
@Service("get_team_main")
public class GetTeamMainServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private TeamUserDao teamUserDao;

    public static final Logger logger = LoggerFactory.getLogger(GetTeamMainServiceImpl.class);

    @Input(required = "team_id")
    @Override
    public void service(Context context) throws Exception {
        String teamId = String.valueOf(context.get("team_id"));
        context.clear();
        int i = teamDao.selectTeamIsExistsByTeamId(teamId);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_FIND_TEAM));
            return;
        }
        try (Jedis redis = jedisPool.getResource();) {
            String key = "team_main_" + teamId;
            String json = redis.get(key);
            TeamMain teamMain;
            if (json == null) {
                teamMain = new TeamMain();
            } else {
                teamMain = new ObjectMapper().readValue(json, TeamMain.class);
            }
            //查询队伍详情信息
            Team team = teamDao.selectTeamInfoLeftJoinTeamAvatarLeftJoinSchoolLeftJoinProvince(teamId);
            teamMain.setTeam(team);
            //查询队伍总人数
            int count = teamUserDao.selectJoinedCountByTeamId(teamId);
            teamMain.setTotal(count);
            context.put("team_main", teamMain);

            //查询前6条队员信息
            //分页
            PageHelper.startPage(1, 6);
            List<TeamUserExtend> teamUserExtends = teamUserDao.selectOneTeamUserInfo2ByTeamId(teamId);
            PageInfo<TeamUserExtend> pageInfo = new PageInfo<>(teamUserExtends);
            context.put("page_info", pageInfo);
        }
        context.put("msg","获取队伍主页");
    }
}
