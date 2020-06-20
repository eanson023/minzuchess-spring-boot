package work.eanson.configuraton;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PropertySourceFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Configuration
@PropertySource(value = "classpath:application.yml", factory = PropertySourceFactory.class)
@ConfigurationProperties(prefix = "redis")
@Setter
public class JedisConfig {

    private String host;

    private Integer port;

    private Integer timeout;

    private Integer maxTotal;

    private Integer maxIdle;

    private Integer minIdle;

    private Integer maxWaitMillis;


    @Bean
    JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxTotal);
        return jedisPoolConfig;
    }

    @Bean
    JedisPool jedisPool() {
        return new JedisPool(jedisPoolConfig(), host, port, timeout);
    }

}
