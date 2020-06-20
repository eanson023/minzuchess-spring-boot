package work.eanson.service.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.ProvinceDao;
import work.eanson.pojo.Province;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;
import work.eanson.util.Result;

import java.util.List;

/**
 * @author eanson
 */
@Service("get_province")
public class GetProvincesServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private ProvinceDao provinceDao;
    @Autowired
    private JedisPool jedisPool;

    @Override
    public void service(Context context) throws Exception {
        String key = "province";
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!jedis.exists(key)) {
                List<Province> provinces = provinceDao.selectAll();
                String value = objectMapper.writeValueAsString(provinces);
                jedis.set(key, value);
                context.setResult(Result.success(provinces));
            } else {
                String json = jedis.get(key);
                List<Province> provinces = objectMapper.readValue(json, new TypeReference<List<Province>>() {
                });
                context.setResult(Result.success(provinces));
            }
        }
    }
}
