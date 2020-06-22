package work.eanson.configuraton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 5个线程  最大10个  队列30个
     *
     * @return
     */
    @Bean
    ExecutorService min5Max10ThreadPool() {
        return new ThreadPoolExecutor(5, 10,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(30), new MessageThreadFactory());
    }

    private class MessageThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger nextId = new AtomicInteger(1);

        /**
         * 定义线程组名称，在 jstack 问题排查时，非常有帮助
         */
        MessageThreadFactory() {
            namePrefix = "From MsgThreadFactory-Worker-";
        }

        @Override
        public Thread newThread(Runnable task) {
            return new Thread(task, namePrefix + nextId.getAndIncrement());
        }
    }
}
