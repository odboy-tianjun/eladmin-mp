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

import cn.odboy.system.core.domain.User;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.annotation.Lock4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 分布式锁 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Lock4jTests {
    @Autowired
    private LockTemplate lockTemplate;

    @Test
    public void contextLoads() {
        // 各种查询操作 不上锁
        String userId = "demo2025";
        // 获取锁
        final LockInfo lockInfo = lockTemplate.lock(userId, 30000L, 5000L);
        if (null == lockInfo) {
            throw new RuntimeException("业务处理中,请稍后再试");
        }
        // 获取锁成功，处理业务
        try {
            System.out.println("执行简单方法1 , 当前线程:" + Thread.currentThread().getName());
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        //结束
    }

    // 用户在5秒内只能访问1次
    @Lock4j(keys = {"#user.id"}, acquireTimeout = 0, expire = 5000, autoRelease = false)
    public Boolean test(User user) {
        return true;
    }

    // 默认获取锁超时3秒，30秒锁过期
    @Lock4j
    public void simple() {
        //do something
    }

    // 完全配置，支持spel
    @Lock4j(keys = {"#user.id", "#user.username"}, expire = 60000, acquireTimeout = 1000)
    public User customMethod(User user) {
        return user;
    }
}
