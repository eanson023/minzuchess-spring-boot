package work.eanson.configuraton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import work.eanson.controller.websocket.ChessBoardInfoEndPoint;
import work.eanson.controller.websocket.ChessLogEndPoint;
import work.eanson.controller.websocket.WebSocketInterceptor;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * https://blog.csdn.net/zmx729618/article/details/78584633
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    TextWebSocketHandler chessBoardInfoEndPoint;

    @Autowired
    TextWebSocketHandler chessLogEndPoint;

    /**
     * 这里一定要注入  如果下面方法是new出来的对象 会导致 之后在WebSocketInterceptor类中无法注入对象的问题
     */
    @Autowired
    WebSocketInterceptor webSocketInterceptor;

    /**
     * 添加连接路径 并支持跨域访问
     *
     * @param webSocketHandlerRegistry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(chessBoardInfoEndPoint, "/jq/global").addInterceptors(webSocketInterceptor).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(chessLogEndPoint, "/jq/log_socket", "/jq/log_socket.ws").addInterceptors(webSocketInterceptor).setAllowedOrigins("*");
    }
}
