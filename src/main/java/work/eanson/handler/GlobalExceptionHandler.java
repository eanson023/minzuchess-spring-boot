package work.eanson.handler;

import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * 全局异常处理
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private SendErrorMessageMailHandler sendErrorMessageMailHandler;

    @Autowired
    private ExecutorService min5Max10ThreadPool;

    /**
     * 全局异常
     * <p>
     * 有异常就向邮箱自己发送信息
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Throwable e) {
        logger.error("ExceptionHandler ===>" + e.getMessage());
        e.printStackTrace();
        // 这里可根据不同异常引起的类做不同处理方式
        String exceptionName = ClassUtils.getShortName(e.getClass());
        logger.error("ExceptionHandler ===>" + exceptionName);
        ModelAndView mav = new ModelAndView();
        mav.addObject("stackTrace", e.getStackTrace());
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("url", req.getRequestURL());

//        发送邮件
        min5Max10ThreadPool.execute(() -> sendErrorMessageMailHandler.sendMailToAdmin(exceptionName, e.getMessage()));
        mav.setViewName("forward:/error/500");
        return mav;
    }


    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Result validateErrorHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> list = new ArrayList<>();
        constraintViolations.forEach(field -> list.add(field.getMessage()));
        return Result.fail("参数校验错误", list);
    }

    /**
     * 没有进行身份验证的异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public String unauthenticatedExceptionHandler(Throwable e) {
        logger.error(e.getMessage());
        return "redirect:/login";
    }
}
