/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;

/**
 * 日志推送到redis队列
 *
 * @author odboy
 * @date 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogPushAppender extends AppenderBase<ILoggingEvent> {
    private String host = "localhost";
    private int port = 6379;
    private String password = null;
    private String queueName = "LoggingQueue";
    private JedisPool jedisPool;
    private Encoder<ILoggingEvent> encoder;

    @Override
    public void start() {
        super.start();
        jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 2000, password);
    }

    @Override
    public void stop() {
        super.stop();
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.lpush(queueName, new String(encoder.encode(eventObject), StandardCharsets.UTF_8));
        } catch (Exception e) {
            addError("Failed to send log to Redis", e);
        }
    }
}
