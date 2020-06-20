package work.eanson.aop;


import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import work.eanson.dao.UserLogDao;
import work.eanson.pojo.UserLog;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;

import java.lang.reflect.Method;

/**
 * 参数校验切面
 *
 * @author yans
 * @date 2020/2/18
 */
@Component
@Aspect
public class InputParamAspect {
    @Autowired
    private UserLogDao userLogDao;

    private static final Logger logger = LoggerFactory.getLogger(InputParamAspect.class);

    @Around(value = "@annotation(work.eanson.service.base.Input)")
    public Object checkParams(ProceedingJoinPoint point) throws Throwable {
        Object reValue;
        boolean flag = this.isLoseParameter(point);
        if (flag) {
            logger.error(MsgCenter.ERROR_PARAMS);
            throw new IllegalArgumentException(MsgCenter.ERROR_PARAMS);
        }
        reValue = point.proceed();
        //用户日志处理
        Context context = (Context) point.getArgs()[0];
        Object msg = context.get("msg");
        if (msg != null) {
            String message = String.valueOf(msg);
            try {
                UserSession userSession = (UserSession) SecurityUtils.getSubject().getPrincipal();
                String host = userSession.getIp();
                String telephone = userSession.getTelephone();
                UserLog userLog = new UserLog();
                userLog.setIp(host);
                userLog.setUserId(telephone);
                userLog.setMessage(message);
                userLogDao.insertSelective(userLog);
                logger.info("用户:" + telephone + "主机:" + host + "\t执行操作:" + message);
            } catch (Exception ignored) {
                return reValue;
            }
        }
        return reValue;
    }

    /**
     * 校验是否缺少参数
     */
    private boolean isLoseParameter(ProceedingJoinPoint point) {
        // 获取方法参数值
        Object[] arguments = point.getArgs();
        // 获取方法
        Method method = getMethod(point);
        // 默认的错误信息
        Input annotation = method.getAnnotation(Input.class);
        String[] required = annotation.required();
        Context contexts = (Context) arguments[0];
        for (String require : required) {
            if (!contexts.containsKey(require) || contexts.get(require) == null) {
                logger.error("缺少参数:" + require);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取方法
     *
     * @param joinPoint ProceedingJoinPoint
     * @return 方法
     */
    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint
                        .getTarget()
                        .getClass()
                        .getDeclaredMethod(joinPoint.getSignature().getName(),
                                method.getParameterTypes());
            } catch (SecurityException | NoSuchMethodException e) {
                logger.error("" + e);
            }
        }
        return method;
    }
}
