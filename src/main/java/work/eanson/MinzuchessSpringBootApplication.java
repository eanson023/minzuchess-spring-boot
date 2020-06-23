package work.eanson;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 民族棋网   Spring-Boot版正式开工
 * <p>
 * 上一个版本有多不合理的地方
 * 1. 异常处理，部分异常应该进行统一的处理，抛出到同一个页面
 * 2.自定义的异常不应该catch 直接抛 要catch就catch自带的异常
 * 3. 邮件发送应该面向组件化 采用JDK原生mail
 * 4. 棋盘规则检测应该面向接口编程，所以需要抽象出一个接口出来
 * 5. 日志改为logback
 * <p>
 * <p>
 * 这次改变
 * 6. 使用thymeleaf
 * 7. 将shiro-redis改为shiro-redis-starter版
 * 8. 使用lombok简化bean编写
 * 9. 使用swapper进行文档编写
 *
 * @author eanson
 * @date 2020/6/14
 */

//文档注解
@EnableSwagger2Doc
//mybatis
@MapperScan("work.eanson.dao")
@SpringBootApplication
public class MinzuchessSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinzuchessSpringBootApplication.class, args);
    }

}
