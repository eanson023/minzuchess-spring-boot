package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.TeamUserDao;
import work.eanson.pojo.back.Message;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;
import work.eanson.util.Result;

import java.util.List;

/**
 * @author eanson
 */
@Service("want_join_message")
public class GetWantJoinMessageServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamUserDao teamUserDao;

    @Override
    public void service(Context context) throws Exception {
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        List<Message> messages = teamUserDao.selectWantJoinTeam(telephone);
        context.setResult(Result.success(messages));
    }
}
