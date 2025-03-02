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
package cn.odboy.modules.logging.aspect;

import cn.odboy.system.logging.domain.SysLog;
import cn.odboy.context.RequestHolder;
import cn.odboy.exception.util.ThrowableUtil;
import cn.odboy.system.logging.service.SysLogService;
import cn.odboy.util.SecurityUtil;
import cn.odboy.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zheng Jie
 * @date 2018-11-24
 */
@Component
@Aspect
@Slf4j
public class LogAspect {
    private final SysLogService sysLogService;
    private final ThreadLocal<Long> currentTime = ThreadLocal.withInitial(System::currentTimeMillis);
    public LogAspect(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(cn.odboy.system.logging.annotation.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            currentTime.set(System.currentTimeMillis());
            return joinPoint.proceed();
        } finally {
            try {
                SysLog sysLog = new SysLog("INFO", System.currentTimeMillis() - currentTime.get());
                HttpServletRequest request = RequestHolder.getHttpServletRequest();
                sysLogService.save(SecurityUtil.safeGetCurrentUsername(), 
                                 StringUtil.getBrowser(request), 
                                 StringUtil.getIp(request), 
                                 joinPoint, 
                                 sysLog);
            } finally {
                currentTime.remove();
            }
        }
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        try {
            if (!(joinPoint instanceof ProceedingJoinPoint)) {
                log.error("JoinPoint类型转换失败");
                return;
            }
            SysLog sysLog = new SysLog("ERROR", System.currentTimeMillis() - currentTime.get());
            sysLog.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            HttpServletRequest request = RequestHolder.getHttpServletRequest();
            sysLogService.save(SecurityUtil.safeGetCurrentUsername(),
                             StringUtil.getBrowser(request),
                             StringUtil.getIp(request),
                             (ProceedingJoinPoint) joinPoint,
                             sysLog);
        } finally {
            currentTime.remove();
        }
    }
}
