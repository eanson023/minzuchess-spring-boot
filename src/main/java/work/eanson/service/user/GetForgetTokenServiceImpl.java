package work.eanson.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.pojo.UserLogin;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.RedisToken;
import work.eanson.util.Result;

/**
 * @author eanson
 * @create 2020-04-01 15:08
 */
@Service("get_for_token")
public class GetForgetTokenServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private RedisToken redisToken;

    @Input(required = "for_token")
    @Override
    public void service(Context context) throws Exception {
        String forToken = String.valueOf(context.get("for_token"));
        UserLogin objectInfo;
        try {
            objectInfo = redisToken.getObjectInfo(forToken, UserLogin.class);
        } catch (Exception e) {
            e.printStackTrace();
            context.setResult(Result.fail(null));
            return;
        }
        context.setResult(Result.success(objectInfo));
    }
}
