package work.eanson.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import work.eanson.dao.ChessInfoDao;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;

import java.util.Arrays;

/**
 * @author eanson
 * 切面 检查棋盘码用
 */
@Component
@Aspect
public class CheckCbCodeAspect {
    @Autowired
    private ChessInfoDao dao;

    private static final Logger logger = LoggerFactory.getLogger(CheckCbCodeAspect.class);

    /**
     * chessService层切面 检测code是否存在
     *
     * @param joinPoint
     * @throws IllegalArgumentException
     */
    @Before("execution(* work.eanson.service.ai.*.*(..))&&!execution(* work.eanson.service.rule.jq.AiTrickAnalyzeServiceImpl.*(..))")
    public void checkCodeExists(JoinPoint joinPoint) throws IllegalArgumentException {
        Object[] args = joinPoint.getArgs();
        Context contexts = (Context) args[0];
        Object codeObj = contexts.get("code");
        if (codeObj == null) {
            throw new IllegalArgumentException(MsgCenter.ERROR_CODE_404);
        }
        String code = String.valueOf(codeObj);
        int i = dao.selectIsExistByPrimaryKey(code);
        if (i == 0) {
            logger.error("查询失败：没有该棋盘码:" + code);
            throw new IllegalArgumentException(MsgCenter.ERROR_CODE_404);
        }
        logger.info("调用:" + joinPoint.getSignature().toString() + "\t参数:" + Arrays.toString(args));
    }
}
