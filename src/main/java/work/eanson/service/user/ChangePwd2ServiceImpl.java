package work.eanson.service.user;

import org.apache.shiro.crypto.hash.Md5Hash;
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
 * @author eanson
 * @create 2020-04-01 15:33
 * 忘记密码2
 */
@Service("update_pwd2")
public class ChangePwd2ServiceImpl extends BaseService implements GlobalService {

    @Input(required = {"password", "repassword", "telephone", "username"})
    @Override
    public void service(Context context) throws Exception {
        String pwd = String.valueOf(context.get("password"));
        String rePwd = String.valueOf(context.get("repassword"));
        String telephone = String.valueOf(context.get("telephone"));
        String username = String.valueOf(context.get("username"));
        if (!pwd.equals(rePwd)) {
            context.setResult(Result.fail(MsgCenter.ERROR_TWICE_PASSWORD));
            return;
        }
        UserLogin userLogin = userLoginDao.selectByPrimaryKey(telephone);
        String oldPwd = userLogin.getPassword();
        String newPwd = new Md5Hash(pwd, username).toHex();
        if (oldPwd.equals(newPwd)) {
            context.setResult(Result.fail(MsgCenter.ERROR_SAVE_PASSWORD));
            return;
        }
        userLogin.setIsRegHalf(null);
        userLogin.setIsDelete(null);
        userLogin.setUsername(null);
        userLogin.setPassword(newPwd);
        userLoginDao.updateByPrimaryKeySelective(userLogin);
        context.setResult(Result.success());
    }
}
