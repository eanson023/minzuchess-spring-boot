package work.eanson.util;

import org.springframework.stereotype.Component;

/**
 * 绑定当线程的相关信息
 * <p>
 * 本项目主要绑定棋盘码
 *
 * @author eanson
 */
@Component
public class ThreadLocalHolder<T> {

    private ThreadLocal<T> threadLocal = new ThreadLocal<>();

    public void set(T t) {
        threadLocal.set(t);
    }

    public T get() {
        return threadLocal.get();
    }

    /**
     * 必须回收自定义的ThreadLocal变量，尤其在线程池场景下，线程经常会被复用，如果不清理自定义的 ThreadLocal变量，
     * 可能会影响后续业务逻辑和造成内存泄露等问题。尽量在代理中使用try-finally块进行回收。
     */
    public void remove() {
        threadLocal.remove();
    }
}
