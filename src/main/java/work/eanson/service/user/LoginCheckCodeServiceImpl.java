package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * 验证码登录
 *
 *
 * 验证redis里面的是否正确
 *
 * @author eanson
 */
@Service("login_check_code")
public class LoginCheckCodeServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private UserLoginDao userLoginDao;

    @Input(required = {"userLogin", "code", "remember_me", "host"})
    @Override
    public void service(Context context) throws Exception {
        UserLogin userLogin = (UserLogin) context.get("userLogin");
        String random = context.get("code") + "";
        String telephone = userLogin.getTelephone();
        boolean rememberMe = (boolean) context.get("remember_me");
        String host = String.valueOf(context.get("host"));
        try (Jedis jedis = jedisPool.getResource();) {
            String key = "code_" + telephone;
//            if (!common.isMobile(telephone)) {
//                context.setResult(Result.fail(MsgCenter.ERROR_PHONE));
//                return;
//            }
            //验证码是否存在
            if (!jedis.exists(key)) {
                context.setResult(Result.fail(MsgCenter.ERROR_CHECK));
                return;
            }
            //redis手机号
            String jTelephone = jedis.hget(key, "telephone");
            //redis验证码
            String jRandom = jedis.hget(key, "random");
            Subject subject = SecurityUtils.getSubject();
            //是否一样
            if (jTelephone.equals(telephone) && jRandom.equals(random)) {
                //删除验证过的信息
                jedis.del(key);
                //登录
                UserLogin dbUserLogin = userLoginDao.selectByPrimaryKey(telephone);
                //手机号不存在
                if (dbUserLogin == null) {
                    context.setResult(Result.fail(MsgCenter.PHONE_NOT_EXISTS));
                    return;
                }
                UsernamePasswordToken token = new UsernamePasswordToken(telephone, dbUserLogin.getPassword(), rememberMe, host);
                //可能会出错
                subject.login(token);
                context.setResult(Result.success());
            } else {
                context.setResult(Result.fail(MsgCenter.ERROR_CHECK_PHONE_CODE));
            }
            context.setResult(Result.success());
            context.put("msg", "手机号登录");
        }
    }
}
