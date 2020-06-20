package work.eanson.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import work.eanson.service.base.ControllerService;
import work.eanson.service.base.SpringContextUtil;
import work.eanson.util.ThreadLocalHolder;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 握手之前先检测棋盘码是否存在
 *
 * @author eanson
 * 创建（handshake）握手前  WebSocket拦截器
 */
@Component
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    public static final String ATTRIBUTE_NAME = "CODE";

    private static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);
    /**
     * 查找棋盘是否存在
     */
    @Autowired
    public ControllerService csgo;

    /**
     * 保存当前线程棋盘码
     */
    @Autowired
    private ThreadLocalHolder<String> threadLocalHolder;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        String host = uri.getHost();
        String query = uri.getQuery();
        if (query == null) {
            logger.error("握手失败:" + MsgCenter.ERROR_PARAMS + "\tip:" + host);
            return false;
        }
        String[] split = query.split("&");
        Map<String, String> params = new HashMap<>();
        for (String s : split) {
            params.put(s.substring(0, s.indexOf('=')), s.substring(s.indexOf('=') + 1));
        }
        String code = params.get("code");
        String alias = params.get("alias");
        if (code == null && alias == null) {
            logger.error("握手失败:" + MsgCenter.ERROR_CODE_404 + "\tip:" + host);
            return false;
        }
        Context context = new Context("check_cb_code");
        if (code != null) {
            context.put("code", code);
            csgo.service(context);
            if (!context.isSuccess()) {
                logger.error("握手失败:" + MsgCenter.ERROR_CODE_404 + "\tip:" + host);
                return false;
            }
            //是否是真人使用
            attributes.put("is_user", true);
        } else {
            context.setNewServiceName("check_cb_alias");
            context.put("alias", alias);
            csgo.service(context);
            if (!context.isSuccess()) {
                logger.error("握手失败:" + MsgCenter.ERROR_CODE_404 + "\tip:" + host);
                return false;
            }
            //是否是观众
            attributes.put("is_user", false);
        }
        code = String.valueOf(context.get("code"));
        //设置棋盘码
        threadLocalHolder.set(code);
        attributes.put(ATTRIBUTE_NAME, code);
        logger.info("握手成功!\tip:" + host);
        attributes.put("test", "yes");
        return true;
    }
}
