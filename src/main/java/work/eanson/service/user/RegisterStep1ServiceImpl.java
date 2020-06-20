package work.eanson.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.crypto.AesCipherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.UserInfoDao;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.IdWorker;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.security.Key;

/**
 * 部分交给Hibernate-validator判断
 * <p>
 * <p>
 * 用户注册
 * 0.判断手机号是否符合规则
 * 1.手机号是否已注册？
 * 1.1已注册，是否只注册了一半
 * 1.2已注册，是否是后台添加？
 * 2.0判断手机号真实姓名 密码
 * 2.手机号 真实姓名 密码
 * 3. 不是管理员 还未加入队伍
 * <p>
 * 4.注册第一步注册成功：存入redis 回写cookie
 * <p>
 * 散列问题:先不加盐不散列 等完成注册之后根据用户名再加盐散列
 *
 * @author eanson
 */
@Service("user_register1")
public class RegisterStep1ServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private UserLoginDao userLoginDao;
    @Autowired
    private UserInfoDao userInfoDao;


    @Autowired
    private IdWorker idWorker;
    @Autowired
    private JedisPool jedisPool;

    @Transactional(rollbackFor = Exception.class)
    @Input(required = {"userLogin", "userInfo"})
    @Override
    public void service(Context context) throws IllegalArgumentException, JsonProcessingException {
        UserLogin userLogin = (UserLogin) context.get("userLogin");
        UserInfo userInfo = (UserInfo) context.get("userInfo");
        context.clear();
        String telephone = userLogin.getTelephone();
        //0
        boolean isRegHalf = false;
        //1
        UserLogin dbLogin = userLoginDao.selectByPrimaryKey(telephone);
        if (dbLogin != null) {
            UserInfo dbInfo = userInfoDao.selectByPrimaryKey(telephone);
            //1.2
            if (dbInfo.getIsAdd()) {
                context.setResult(Result.fail(MsgCenter.USER_IS_ADD));
                return;
            } else {
                //1.1是否只注册了一半
                if (dbLogin.getIsRegHalf()) {
                    isRegHalf = true;
                } else {
                    //已存在
                    context.setResult(Result.fail(MsgCenter.USER_EXISTS));
                    return;
                }
            }
        }
        String realName = userInfo.getRealName();
        String password = userLogin.getPassword();
        //2.0
        //2保存信息到数据库  该步骤换到FinishRegisterService里
        //2.1生成规则用户名规则
        //改到最后结束注册的时候再做 目的保证数据同步
//        String username = common.generateAliasCode() + "";
//        暂时用原密码
        userLogin.setPassword(password);
        userLogin.setIsRegHalf(true);
        //2.2保存至user_login表
        if (isRegHalf) {
            userLoginDao.updateByPrimaryKeySelective(userLogin);
            userInfoDao.updateByPrimaryKeySelective(userInfo);
        } else {
            //设置uuid用于查询用
            userInfo.setUuid(getUUID());
            userLoginDao.insertSelective(userLogin);
            userInfoDao.insertSelective(userInfo);
        }
        //2.3将redis序列号加密回写加入cookie
        AesCipherService aesCipherService = new AesCipherService();
        //设置key长度
        aesCipherService.setKeySize(128);
        //生成key
        Key key = aesCipherService.generateNewKey();
        //雪花算法key
        String text = idWorker.nextId() + "";
        byte[] encoded = key.getEncoded();
        //加密
        String encrptText = aesCipherService.encrypt(text.getBytes(), encoded).toHex();
        //写入redis
        Jedis jedis = jedisPool.getResource();
        //直接写成json
        UserSession userSession = new UserSession();
        userSession.setTelephone(telephone);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(userSession);
        jedis.set(text, s);
        jedis.expire(text, 30 * 60);
        //将密钥存入redis 键为生成的token的hash值
        String secKey = encrptText.hashCode() + "";
        //作为链表push
        for (byte b : encoded) {
            jedis.rpush(secKey, String.valueOf(b));
        }
        jedis.expire(secKey, 30 * 60);
        context.put("reg_token", encrptText);
        //将拟定队名放入redis
        String exToken = idWorker.nextId() + "";
        jedis.set(exToken, realName + "队");
        jedis.expire(exToken, 30 * 60);
        context.put("ex_token", exToken);
        context.setResult(Result.success());

        jedis.close();
    }

}
