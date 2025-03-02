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
package cn.odboy.modules.quartz.context;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.system.tools.model.EmailVo;
import cn.odboy.context.SpringBeanHolder;
import cn.odboy.exception.util.ThrowableUtil;
import cn.odboy.system.quartz.domain.QuartzJob;
import cn.odboy.system.quartz.domain.QuartzLog;
import cn.odboy.modules.quartz.mapper.QuartzLogMapper;
import cn.odboy.system.quartz.service.QuartzJobService;
import cn.odboy.system.tools.repository.EmailRepository;
import cn.odboy.util.RedisUtil;
import cn.odboy.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 参考人人开源，<a href="https://gitee.com/renrenio/renren-security">...</a>
 *
 * @author /
 * @date 2019-01-07
 */
@Async
public class ExecutionJob extends QuartzJobBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 此处仅供参考，可根据任务执行情况自定义线程池参数
     */
    private final ThreadPoolTaskExecutor executor = SpringBeanHolder.getBean("elAsync");

    @Override
    public void executeInternal(JobExecutionContext context) {
        // 获取任务
        QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        // 获取spring bean
        QuartzLogMapper quartzLogMapper = SpringBeanHolder.getBean(QuartzLogMapper.class);
        QuartzJobService quartzJobService = SpringBeanHolder.getBean(QuartzJobService.class);
        RedisUtil redisUtil = SpringBeanHolder.getBean(RedisUtil.class);
        String uuid = quartzJob.getUuid();
        QuartzLog log = new QuartzLog();
        log.setJobName(quartzJob.getJobName());
        log.setBeanName(quartzJob.getBeanName());
        log.setMethodName(quartzJob.getMethodName());
        log.setParams(quartzJob.getParams());
        long startTime = System.currentTimeMillis();
        log.setCronExpression(quartzJob.getCronExpression());
        try {
            // 执行任务
            QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(), quartzJob.getParams());
            Future<?> future = executor.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            if (StringUtil.isNotBlank(uuid)) {
                redisUtil.set(uuid, true);
            }
            // 任务状态
            log.setIsSuccess(true);
            logger.info("任务执行成功，任务名称：{}, 执行时间：{}毫秒", quartzJob.getJobName(), times);
            // 判断是否存在子任务
            if (StringUtil.isNotBlank(quartzJob.getSubTask())) {
                String[] tasks = quartzJob.getSubTask().split("[,，]");
                // 执行子任务
                quartzJobService.executionSubJob(tasks);
            }
        } catch (Exception e) {
            if (StringUtil.isNotBlank(uuid)) {
                redisUtil.set(uuid, false);
            }
            logger.error("任务执行失败，任务名称：{}", quartzJob.getJobName());
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            // 任务状态 0：成功 1：失败
            log.setIsSuccess(false);
            log.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            // 任务如果失败了则暂停
            if (quartzJob.getPauseAfterFailure() != null && quartzJob.getPauseAfterFailure()) {
                quartzJob.setIsPause(false);
                //更新状态
                quartzJobService.updateIsPause(quartzJob);
            }
            if (quartzJob.getEmail() != null) {
                EmailRepository emailRepository = SpringBeanHolder.getBean(EmailRepository.class);
                // 邮箱报警
                if (StringUtil.isNoneBlank(quartzJob.getEmail())) {
                    EmailVo emailVo = taskAlarm(quartzJob, ThrowableUtil.getStackTrace(e));
                    emailRepository.send(emailVo);
                }
            }
        } finally {
            quartzLogMapper.insert(log);
        }
    }

    private EmailVo taskAlarm(QuartzJob quartzJob, String msg) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("定时任务【" + quartzJob.getJobName() + "】执行失败，请尽快处理！");
        Map<String, Object> data = new HashMap<>();
        data.put("task", quartzJob);
        data.put("msg", msg);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("taskAlarm.ftl");
        emailVo.setContent(template.render(data));
        List<String> emails = Arrays.asList(quartzJob.getEmail().split("[,，]"));
        emailVo.setTos(emails);
        return emailVo;
    }
}
