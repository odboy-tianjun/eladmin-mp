package me.zhengjie.context;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gitlab支持
 *
 * @author odboy
 * @date 2025-01-13
 */
@Configuration
@EnableConfigurationProperties(GitlabProperties.class)
@ConditionalOnClass(GitlabAuthRepository.class)
public class GitlabAutoConfiguration {
    @Bean
    public GitlabAuthRepository gitlabAuthRepository() {
        return new GitlabAuthRepository();
    }
}