package work.eanson.service.base;


import org.springframework.stereotype.Service;
import work.eanson.util.Context;

/**
 * 控制层和业务层桥梁类
 * <p>
 * 方法名不能重载
 * <p>
 * 所有service业务 实现该接口 然后获取bean向下转型 多态的表现
 * <p>
 * <p>
 * 这样做的好处是 更好地解耦，但是 实际操作起来非常的麻烦
 * <p>
 * 每次获取bean都要指定bean的名称 IDE也不会帮你找
 *
 * @author jjy
 */
@SuppressWarnings("unchecked")
@Service
public class ControllerService {

    public void service(Context context) throws Exception {
        GlobalService globalService = SpringContextUtil.getBean(context.getServiceName());
        globalService.service(context);
    }
}
