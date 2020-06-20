package work.eanson.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.eanson.util.ThreadLocalHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制层日志打印  方便debug
 *
 * @author eanson
 */
@Component
@Aspect
public class ControllerAspectJ {

    @Autowired
    private ThreadLocalHolder<HttpServletRequest> threadLocalHolder;

    private static final Logger logger = LoggerFactory.getLogger(ControllerAspectJ.class);

    @Pointcut("execution(* work.eanson.controller.*.*(..))&&!execution(* work.eanson.controller.AbstractController.*(..))")
    public void point() {
    }

    @Before(value = "point()")
    public void logAccess(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ip:").append(request.getRemotePort()).append("\tmethod:").append(request.getMethod()).append("\turl:").append(request.getRequestURL()).append("\tparams:");
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                stringBuilder.append("\t").append(arg.getClass().getSimpleName()).append(" ").append(arg.toString());
            }
        }
        logger.info(stringBuilder.toString());
    }

    /**
     * 删除ThreadLocal变量
     *
     *
     * 如果不清理自定义的 ThreadLocal变量，
     * 可能会影响后续业务逻辑和造成内存泄露等问题。尽量在代理中使用try-finally块进行回收
     */
    @After(value = "point()")
    public void after() {
        threadLocalHolder.remove();
    }


}
