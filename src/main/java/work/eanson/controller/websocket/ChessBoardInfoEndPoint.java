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
import work.eanson.handler.SendChessMessageHandler;
import work.eanson.service.base.ControllerService;
import work.eanson.util.ThreadLocalHolder;
import work.eanson.util.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * 与无数张棋盘建立连接
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Controller
public class ChessBoardInfoEndPoint extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChessBoardInfoEndPoint.class);

    /**
     * 装当前线程棋盘码
     */
    @Autowired
    private ThreadLocalHolder<String> threadLocalHolder;

    @Autowired
    private ControllerService csgo;


    @Autowired
    private SendChessMessageHandler sendChessMessageHandler;


    /**
     * 线程池
     */
    @Autowired
    ExecutorService cachedThreadPool;

    /**
     * 使用的用户
     * key:sessionId,
     * value session
     */
    public static Map<String, WebSocketSession> usingClients = new ConcurrentHashMap<>();
    /**
     * 观看的用户
     */
    public static Map<String, WebSocketSession> watchingClients = new ConcurrentHashMap<>();


    /**
     * 连接成功
     * <p>
     * 发送当前消息
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String code = threadLocalHolder.get();
        try {
            //添加新用户
            if (isUser(session)) {
                logger.info("使用者加入：" + session.getRemoteAddress() + "\tid:" + session.getId());
                usingClients.put(session.getId(), session);
            } else {
                logger.info("新观众加入：" + session.getRemoteAddress() + "\tid:" + session.getId());
                watchingClients.put(session.getId(), session);
            }
            Context context = new Context("get_set_chess");
            context.put("code", code);
            csgo.service(context);
            if (session.isOpen()) {
                //发送当前棋盘信息
                session.sendMessage(new TextMessage(String.valueOf(context.getResult().getData())));
            }
        } finally {
            threadLocalHolder.remove();
        }

    }

    /**
     * 收到消息
     * 跟新缓存数据
     * 利用set集合去重复
     * 更新pos
     * 群发
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Context context = new Context("update_cache");
        String code = String.valueOf(session.getAttributes().get(WebSocketInterceptor.ATTRIBUTE_NAME));
        //每次请求线程不一样 不能用这个
//        chess.set(codeHolder.get());
        context.put("code", code);
        context.put("pos", message.getPayload());
        csgo.service(context);
        context.setNewServiceName("get_set_chess");
        csgo.service(context);
        /*
         * 向观众群发信息
         */
        try {
            threadLocalHolder.set(code);
//            发送
            sendChessMessageHandler.broadcast(watchingClients.values(), String.valueOf(context.getResult().getData()));
            /**
             * 还要向玩家发 可能是多人对战 另外一名晚间也需要接收 所以这里统一发到所有使用者页面上
             */
            sendChessMessageHandler.broadcast(usingClients.values(), String.valueOf(context.getResult().getData()));
        } finally {
            threadLocalHolder.remove();
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket传输错误:", exception);
    }

    /**
     * 用户或者观众断开连接
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (isUser(session)) {
            logger.info("用户退出:" + session.getRemoteAddress());
            //删除用户
            usingClients.remove(session.getId());
            //如果没有人了 直接清除记谱缓存
            if (usingClients.size() == 0) {
                Context context = new Context("clear_cache");
                String code = String.valueOf(session.getAttributes().get(WebSocketInterceptor.ATTRIBUTE_NAME));
                context.put("code", code);
                csgo.service(context);
                logger.info("清空记谱缓存");
            }
        } else {
            logger.info("观众退出:" + session.getRemoteAddress());
            //删除观众
            watchingClients.remove(session.getId());
        }
    }

    /**
     * 是否是用户
     *
     * @param session websocket session
     * @return
     */
    private boolean isUser(WebSocketSession session) {
        Object isUser = session.getAttributes().get("is_user");
        return isUser != null && (boolean) isUser;
    }
}
