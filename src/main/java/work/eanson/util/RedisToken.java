package work.eanson.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.ParserException;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.pojo.back.UserSession;
import work.eanson.pojo.security.SecurityInfo;

import java.io.IOException;
import java.security.Key;
import java.util.List;

/**
 * @author eanson
 */
@Component
public class RedisToken {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private IdWorker idWorker;

    private SecurityInfo getJiemiInfo(String token) {
        //1.得到token的hash值 根据hash值得到密钥
        try (Jedis jedis = jedisPool.getResource()) {
            String securityKey = token.hashCode() + "";
            List<String> lrange = jedis.lrange(securityKey, 0, -1);
            if (lrange.isEmpty()) {
                throw new ParserException("没有token");
            }
            byte[] keyBytes = new byte[lrange.size()];
            for (int i = 0; i < lrange.size(); i++) {
                keyBytes[i] = Byte.parseByte(lrange.get(i));
            }
            lrange.clear();
            AesCipherService aesCipherService = new AesCipherService();
            //得到存有电话的键 解密
            String sessionKey = new String(aesCipherService.decrypt(Hex.decode(token), keyBytes).getBytes());
            //手机号
            String session = jedis.get(sessionKey);
            //关闭
            return new SecurityInfo(securityKey, sessionKey, session);
        }
    }

    /**
     * //获取securityKey, sessionKey
     *
     * @param token
     * @return
     */
    public SecurityInfo getSecurityInfo(String token) {
        SecurityInfo jiemiInfo = getJiemiInfo(token);
        jiemiInfo.setInfo(null);
        return jiemiInfo;
    }

    /**
     * //获取session中手机号
     *
     * @param token
     * @return
     */
    public String getSessionTelephone(String token) {
        return getSession(token).getTelephone();
    }

    /**
     * //获取session
     *
     * @param token
     * @return
     */
    public UserSession getSession(String token) {
        SecurityInfo jiemiInfo = getJiemiInfo(token);
        ObjectMapper objectMapper = new ObjectMapper();
        UserSession userSession = null;
        try {
            userSession = objectMapper.readValue(jiemiInfo.getInfo(), UserSession.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userSession;
    }

    public <T> T getObjectInfo(String token, Class<T> requiredClazz) {
        SecurityInfo jiemiInfo = getJiemiInfo(token);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jiemiInfo.getInfo(), requiredClazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateSession(String token, UserSession userSession) {
        SecurityInfo securityInfo = getSecurityInfo(token);
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String s = objectMapper.writeValueAsString(userSession);
                jedis.set(securityInfo.getSessionKey(), s);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


    public void delToken(SecurityInfo securityInfo) {
        String securityKey = securityInfo.getSecurityKey();
        String sessionKey = securityInfo.getSessionKey();
        try (Jedis resource = jedisPool.getResource();) {
            resource.del(securityKey, sessionKey);
        }
    }

    public void delToken(String token) {
        delToken(getJiemiInfo(token));
    }

    /**
     * @param value
     * @return token
     */
    public String jiami(Object value) throws JsonProcessingException {
        try (Jedis jedis = jedisPool.getResource()) {
            //2.3将redis序列号加密回写加入cookie
            AesCipherService aesCipherService = new AesCipherService();
            //设置key长度
            aesCipherService.setKeySize(128);
            //生成key
            Key key = aesCipherService.generateNewKey();
            //雪花算法key
            String idKey = idWorker.nextId() + "";
            byte[] encoded = key.getEncoded();
            //加密
            String token = aesCipherService.encrypt(idKey.getBytes(), encoded).toHex();
            //写入redis
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(value);
            jedis.set(idKey, s);
            jedis.expire(idKey, 30 * 60);
            //将密钥存入redis 键为生成的token的hash值
            String secKey = token.hashCode() + "";
            //作为链表push
            for (byte b : encoded) {
                jedis.rpush(secKey, String.valueOf(b));
            }
            jedis.expire(secKey, 30 * 60);
            //将拟定队名放入redis
            return token;
        }
    }
}
