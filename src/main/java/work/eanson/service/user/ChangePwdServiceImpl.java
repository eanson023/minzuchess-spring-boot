package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;
import work.eanson.pojo.UserLogin;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * 更新密码
 *
 * @author eanson
 */
@Service("update_pwd")
public class ChangePwdServiceImpl extends BaseService implements GlobalService {

    @Input(required = {"original", "password"})
    @Override
    public void service(Context context) throws Exception {
        String original = context.get("original") + "";
        String password = context.get("password") + "";
        context.clear();


        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        UserLogin userLogin = userLoginDao.selectByPrimaryKey(telephone);
        String username = userLogin.getUsername();
        //进行加密比较的密码
        String outPwd = new Md5Hash(original, username).toHex();
        original = userLogin.getPassword();
        if (!original.equals(outPwd)) {
            context.setResult(Result.fail(MsgCenter.ERROR_ORIGINAL_PASSWORD));
            return;
        }
//        //判断密码是否符合
//        if (!common.isPassword(password)) {
//            context.setResult(Result.fail(MsgCenter.ERROR_PASSWORD_FORMAT));
//            return;
//        }
        String newPwd = new Md5Hash(password, username).toHex();
        if (newPwd.equals(original)) {
            context.setResult(Result.fail(MsgCenter.ERROR_SAVE_PASSWORD));
            return;
        }
        UserLogin userLogin1 = new UserLogin();
        userLogin1.setTelephone(telephone);
        userLogin1.setPassword(newPwd);
        userLoginDao.updateByPrimaryKeySelective(userLogin1);
        context.setResult(Result.success());
        context.put("msg","更改密码");
    }
}
