package work.eanson.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import work.eanson.controller.websocket.WebSocketInterceptor;
import work.eanson.util.ThreadLocalHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Component
public class SendChessMessageHandler {

    @Autowired
    private ThreadLocalHolder<String> codeHolder;

    @Autowired
    private ExecutorService min5Max10ThreadPool;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    /**
     * 发送消息
     *
     * @param webSocketSessions 所有连接
     * @param msg               消息
     */
    public void broadcast(Collection<WebSocketSession> webSocketSessions, String msg) {
        final String code = codeHolder.get();
        TextMessage message = new TextMessage(msg);
        webSocketSessions.forEach(ws -> {
            //            获取CODE属性的值 如果与当前棋盘码相等 则推送消息
            if (ws.getAttributes().get(WebSocketInterceptor.ATTRIBUTE_NAME).equals(code)) {
                min5Max10ThreadPool.execute(() -> {
                    try {
                        logger.info("向ip:" + ws.getRemoteAddress() + "发送消息:" + message);
                        ws.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("向ip：" + ws.getRemoteAddress() + " 发送信息失败");
                    }
                });
            }
        });
    }


}
