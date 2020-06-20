package work.eanson.service.base;

import java.lang.annotation.*;


/**
 * @author eanson
 * 参数校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Input {
    /**
     * 必选参数
     *
     * @return
     */
    String[] required() default {};

    /**
     * 可选参数
     *
     * @return
     */
    String[] optional() default {};

}
