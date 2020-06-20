package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.TeamUserDao;
import work.eanson.dao.UserInfoDao;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.Message;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

import java.util.List;

/**
 * @author eanson
 * 同意或者拒绝
 */
@Service("resolve_join")
public class ResolveJoinServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;
    @Autowired
    private UserInfoDao userInfoDao;


    @Input(required = {"is_agree", "team_id", "uuid"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        String teamId = String.valueOf(context.get("team_id"));
        boolean isAgree = (boolean) context.get("is_agree");
        String uuid = String.valueOf(context.get("uuid"));
        context.clear();
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        Boolean aBoolean = teamUserDao.checkIsLeader(teamId, telephone);
        if (aBoolean == null) {
            return;
        }
        if (!aBoolean) {
            return;
        }
        String s = userInfoDao.selectPrimaryKeyByUUID(uuid);
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(teamId);
        teamUser.setUserId(s);
        //同意
        if (isAgree) {
            teamUser.setIsJoin(true);
        }
        //拒绝
        else {
            teamUser.setIsRefuse(true);
        }
        teamUserDao.updateUserIdAndTeamIdSelective(teamUser);

        //更新session
        UserSession userSession = (UserSession) SecurityUtils.getSubject().getPrincipal();
        //重写equals hashcode
        Message message = new Message();
        message.setUuid(uuid);
        message.setTeamId(teamId);
        List<Message> messages = userSession.getMessages();
        messages.remove(message);
        userSession.setMsgCount(userSession.getMsgCount() - 1);
        //更新
        updateNewSession(userSession);

        context.put("msg", "处理加入队伍请求");
    }
}
