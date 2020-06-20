package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.UserLogDao;
import work.eanson.pojo.UserLog;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * 登录 并且记录日志
 *
 * @author eanson
 */
@Service("logout")
public class LogoutServiceImpl extends BaseService implements GlobalService {

    @Input
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        context.put("msg", "登出");
    }
}
