package work.eanson.service.admin;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.CategoryDao;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.UserInfoDao;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.Category;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;

import java.util.List;


/**
 * @author eanson
 * @create 2020-03-29 18:57
 * 添加用户 添加棋盘码
 */
@Service("admin_add_user")
public class AddUserServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private UserLoginDao userLoginDao;
    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ChessInfoDao chessInfoDao;

    @Input(required = {"user_info", "user_login"})
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        UserInfo userInfo = (UserInfo) context.get("user_info");
        UserLogin userLogin = (UserLogin) context.get("user_login");
        context.clear();
        String telephone = userInfo.getTelephone();
        UserLogin userLogin1 = userLoginDao.selectByPrimaryKey(telephone);
        if (userLogin1 != null) {
            context.put("msg", MsgCenter.PHONE_EXISTS);
            return;
        }
        long base = generateAliasCode();
        String username = String.valueOf(base);
        String password = userLogin.getPassword();
        //2.2密码加密 用户名作为盐值
        password = new Md5Hash(password, username).toHex();
        userLogin.setPassword(password);
        userLogin.setUsername(username);
        userLogin.setIsDelete(false);
        userLogin.setIsRegHalf(false);
        userInfo.setUuid(getUUID());
        userInfo.setIsAdmin(false);
        userInfo.setIsAdd(true);
        userInfo.setStat((byte) 1);
        userLoginDao.insertSelective(userLogin);
        userInfoDao.insertSelective(userInfo);
        //添加棋盘码
        List<Category> categories = categoryDao.selectAll();
        ChessInfo chessInfo = new ChessInfo();
        chessInfo.setUserId(telephone);
        chessInfo.setIsPublic(false);
        chessInfo.setClock("0Z:" + System.currentTimeMillis());
        for (Category category : categories) {
            chessInfo.setCategory(category.getKey());
            chessInfo.setPos(category.getInitPos());
            chessInfo.setCode(getRandomCode());
            chessInfo.setAlias(base);
            base++;
            chessInfoDao.insert(chessInfo);
        }
        context.put("msg", "添加成功,用户名:" + username);
    }

}
