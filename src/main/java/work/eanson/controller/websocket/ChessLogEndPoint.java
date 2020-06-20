package work.eanson.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import work.eanson.util.ThreadLocalHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
@Controller
public class ChessLogEndPoint extends TextWebSocketHandler {

    @Autowired
    private ThreadLocalHolder<String> threadLocalHolder;
    /**
     * 连接的用户
     */
    public static Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ChessLogEndPoint.class);


    /**
     * 连接成功
     * 发送10条当前棋盘码日志
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String code = codeHolder.get();
        //添加 到map中
        clients.put(session.getId(), session);
//        if (session.isOpen()) {
//            String string = objectMapper.writeValueAsString(result);
//            TextMessage textMessage = new TextMessage(string);
//            //发送当前棋盘信息
//            session.sendMessage(textMessage);
//        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket传输错误:", exception);
    }

    /**
     * 用户断开连接
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("断开连接:" + session.getRemoteAddress());
        clients.remove(session.getId());
    }
}
