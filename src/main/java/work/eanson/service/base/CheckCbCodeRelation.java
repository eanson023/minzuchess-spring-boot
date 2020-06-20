package work.eanson.service.base;

import java.lang.annotation.*;

/**
 * @author eanson
 * @create 2020-03-30 22:01
 * 检查旗棋盘码状态 公开 未公开
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckCbCodeRelation {
}
