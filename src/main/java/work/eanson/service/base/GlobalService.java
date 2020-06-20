package work.eanson.service.base;

import work.eanson.util.Context;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
public interface GlobalService {
    /**
     * 所有controller层调用此抽象service方法然后向下转型
     *
     * @param context 装载数据的工具
     * @throws Exception 无
     */
    void service(Context context) throws Exception;
}
