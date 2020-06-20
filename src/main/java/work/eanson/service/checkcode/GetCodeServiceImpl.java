package work.eanson.service.checkcode;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.io.IOException;
import java.util.Map;

/**
 * 获取验证码
 *
 * @author eanson
 */
@Service("get_code")
@ConfigurationProperties(prefix = "aliyun")
@Setter
public class GetCodeServiceImpl extends BaseService implements GlobalService {

    private final Logger logger = LoggerFactory.getLogger(GetCodeServiceImpl.class);

    private String accessKeyId;

    private String secret;

    private String signName;

    private String templateCode;

    @Autowired
    private JedisPool jedisPool;

    @Input(required = {"telephone", "is_new"})
    @Override
    public void service(Context context) {
        String telephone = context.get("telephone") + "";
        boolean isNew = (boolean) context.get("is_new");
        //判断是否是新用户登录,若不是，则不去获取手机号  目的：防止用户未注册用户登录
        if (!isNew) {
            UserLogin userLogin = userLoginDao.selectByPrimaryKey(telephone);
            if (userLogin == null) {
                context.clear();
                context.setResult(Result.fail(MsgCenter.PHONE_NOT_EXISTS));
                return;
            }
        }
        //判断是否是已注册的用户获取验证码
        else {
            int num = userLoginDao.selectIsFinishRegUser(telephone);
            if (num != 0) {
                context.clear();
                context.setResult(Result.fail(MsgCenter.USER_EXISTS));
                return;
            }
        }
        //判断redis里面是否有验证码,如果有的话就不发送 避免重复发送
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "code_" + telephone;
            if (jedis.exists(key)) {
                context.setResult(Result.fail(MsgCenter.ERROR_CHECK_SAME));
                return;
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        //4位随机数
        String random = (int) ((Math.random() * 9 + 1) * 1000) + "";
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", telephone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + random + "\"}");
        Result result = null;
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map map = objectMapper.readValue(response.getData(), Map.class);
            if ("OK".equals(map.get("Code"))) {
                context.put("random", random);
                result = Result.success();
                logger.info("发送验证码至：" + telephone + "成功");
            } else {
                logger.error("发送短信错误,错误信息：" + response.getData() + "\t电话：" + telephone + "\t验证码:" + random);
                result = Result.fail(MsgCenter.ERROR);
            }
        } catch (ClientException | IOException e) {
            e.printStackTrace();
        }
        context.setResult(result);
    }
}
