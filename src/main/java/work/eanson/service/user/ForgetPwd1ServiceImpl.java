package work.eanson.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.*;


/**
 * @author eanson
 * @create 2020-04-01 14:39
 */
@Service("forget_pwd")
public class ForgetPwd1ServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private RedisToken redisToken;

    @Input(required = "telephone")
    @Override
    public void service(Context context) throws Exception {
        String telephone = String.valueOf(context.get("telephone"));
        UserLogin userLogin = userLoginDao.selectByPrimaryKey(telephone);
        //没有注册
        if (userLogin == null) {
            context.setResult(Result.fail(MsgCenter.PHONE_NOT_EXISTS));
            return;
        }
        //只注册了一半
        if (userLogin.getIsRegHalf()) {
            context.setResult(Result.fail(MsgCenter.ERROR_REGISTER_ONLY_HALF));
            return;
        }
        userLogin.setIsRegHalf(null);
        userLogin.setIsDelete(null);
        userLogin.setPassword(null);
        String jiami = redisToken.jiami(userLogin);
        context.setResult(Result.success(jiami));
    }
}
