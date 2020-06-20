package work.eanson.service.user;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
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
 * 保存会话信息
 * 并添加登录日志
 *
 * @author eanson
 */
@Service("save_session")
public class SaveSessionServiceImpl extends BaseService implements GlobalService {

    @Autowired
    private UserLogDao userLogDao;

    private static final Logger logger = LogManager.getLogger(SaveSessionServiceImpl.class);

    @Input(required = {"ip"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
//        UserSession userSession = (UserSession) context.get("userSession");
        String ip = context.get("ip") + "";
//        boolean rememberMe = userSession.getRememberMe();
//        //加密\
//
//        String idWork = idWorker.nextId() + "";
//        AesCipherService aesCipherService = new AesCipherService();
//        //设置key长度
//        aesCipherService.setKeySize(128);
//        //生成key
//        Key key = aesCipherService.generateNewKey();
//        byte[] encoded = key.getEncoded();
//        //加密
//        String newToken = aesCipherService.encrypt(idWork.getBytes(), encoded).toHex();
//        //设置token
//        userSession.setToken(newToken);
//        String hashKey = newToken.hashCode() + "";
//        //获取密钥
//        byte[] encoded1 = key.getEncoded();
//
//        //redis
//        try (Jedis jedis = jedisPool.getResource()) {
//            //放入密钥
//
//            for (byte b : encoded1) {
//                jedis.rpush(hashKey, String.valueOf(b));
//            }
//            //存入json序列化信息
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                String sessionInfo = objectMapper.writeValueAsString(userSession);
//                jedis.set(idWork, sessionInfo);
//                //一天
//                if (rememberMe) {
//                    jedis.expire(idWork, 60 * 60 * 24);
//                    jedis.expire(hashKey, 60 * 60 * 24);
//                } else {
//                    jedis.expire(idWork, 60 * 30);
//                    jedis.expire(hashKey, 60 * 30);
//                }
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        context.put("userSession", userSession);
        //添加日志
        UserLog userLog = new UserLog();
        userLog.setIp(ip);
        userLog.setMessage("登录");
        UserSession userSession = (UserSession) SecurityUtils.getSubject().getPrincipal();
        userLog.setUserId(userSession.getTelephone());
        userLogDao.insertSelective(userLog);
        logger.info("保存会话并且添加日志完毕:" + userSession.getToken());
    }
}
