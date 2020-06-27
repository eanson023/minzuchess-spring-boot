package work.eanson.service.realm;


import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.AvatarDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.dao.UserInfoDao;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.Avatar;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.pojo.back.Message;
import work.eanson.pojo.back.UserSession;

import java.util.Collections;
import java.util.List;

/**
 * @author eanson
 * 登录验证
 */
public class LoginRealm extends AuthorizingRealm {
    @Autowired
    private UserLoginDao userLoginDao;

    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private TeamUserDao teamUserDao;

    @Autowired
    private AvatarDao avatarDao;


    @Autowired
    private JedisPool jedisPool;

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        UserSession userSession = (UserSession) principalCollection.getPrimaryPrincipal();
        String telephone = userSession.getTelephone();
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(telephone);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (userInfo.getIsAdmin()) {
            info.setRoles(Collections.singleton("admin"));
        }
        return info;
    }

    /**
     * 验证
     * SimpleAuthenticationInfo 与 UsernamePasswordToken里的密码比较
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //转型
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String telephone = usernamePasswordToken.getPrincipal().toString();
        UserLogin userLogin = userLoginDao.selectByPrimaryKey(telephone);
        if (userLogin == null) {
            return null;
        }
        //只注册了一半 可以抛出异常
        if (userLogin.getIsRegHalf()) {
            throw new LockedAccountException("用户未完成注册");
        }
        String password = userLogin.getPassword();
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(telephone);


        //查找头像
        Avatar avatar = avatarDao.selectByForeignKey(telephone);
        //封装userSession
        UserSession userSession = new UserSession();
        userSession.setRealName(userInfo.getRealName());
        userSession.setTelephone(telephone);
        //查找是否有消息
        int msgCount = teamUserDao.selectWantJoinMegCount(telephone);
        userSession.setMsgCount(msgCount);
        if (msgCount > 0) {
            List<Message> messages = teamUserDao.selectWantJoinTeam(telephone);
            userSession.setMessages(messages);
        }
        //删除的多余的消息
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "update_message_" + telephone;
            if (jedis.exists(key)) {
                jedis.del(key);
            }
        }
        if (avatar != null) {
            userSession.setAvatar(avatar.getFileName());
        }
        //token
        //获取是否记住我
        boolean rememberMe = usernamePasswordToken.isRememberMe();
        userSession.setRememberMe(rememberMe);
        userSession.setUsername(userLogin.getUsername());
        String host = usernamePasswordToken.getHost();
        userSession.setIp(host);


        //返回信息 1.用户信息 2.密码 4. realm名称 验证在shiro.xml中配置
        return new SimpleAuthenticationInfo(userSession, password, super.getName());
    }
}
