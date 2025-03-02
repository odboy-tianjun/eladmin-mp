/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.config.websocket.core;

import cn.odboy.config.websocket.model.SocketMsg;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHouYing
 * @date 2019-08-10 15:46
 */
@ServerEndpoint("/webSocket/{sid}")
@Slf4j
@Component
public class WebSocketServer {
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。（分布式必出问题）
     */
    private static final Map<String, WebSocketServer> CLIENT = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收sid
     */
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        // 如果存在就先删除一个，防止重复推送消息
        CLIENT.remove(sid);
        CLIENT.put(sid, this);
        this.sid = sid;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        CLIENT.remove(this.sid);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来{}的信息:{}", sid, message);
        sendToAll(message);
    }

    /**
     * 群发消息
     *
     * @param message /
     */
    private static void sendToAll(String message) {
        //
        for (WebSocketServer item : CLIENT.values()) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                log.error("发送消息给{}失败", item.sid, e);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(SocketMsg socketMsg, @PathParam("sid") String sid) throws IOException {
        String message = JSON.toJSONString(socketMsg);
        log.info("推送消息到{}，推送内容:{}", sid, message);
        try {
            if (sid == null) {
                sendToAll(message);
            } else {
                CLIENT.get(sid).sendMessage(message);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session) &&
                Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sid);
    }
}
