package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;


/**
 * 密码登录
 *
 * @author eanson
 */
@Service("login_pwd")
public class LoginPwdServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private UserLoginDao userLoginDao;

    @Input(required = {"userLogin", "remember_me", "host"})
    @Override
    public void service(Context context) throws AuthenticationException {
        UserLogin userLogin = (UserLogin) context.remove("userLogin");
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();
        //根据用户名查询 用户信息
        userLogin = userLoginDao.selectByUserName(username);
        if (userLogin == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_USERNAME_OR_PASSWORD));
            return;
        }
        String telephone = userLogin.getTelephone();
        // 需要手动将密码散列
        //1. 密码 2 盐
        password = new Md5Hash(password, username).toHex();
        //是否记住我
        boolean rememberMe = (boolean) context.get("remember_me");
        String host = String.valueOf(context.get("host"));
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(telephone, password, rememberMe, host);
        //登录
        subject.login(token);
        context.setResult(Result.success());
        context.put("msg", "密码登录");
    }
}
