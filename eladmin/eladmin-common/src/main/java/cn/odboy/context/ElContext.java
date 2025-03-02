package cn.odboy.context;

import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.ElConstant;
import lombok.Data;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 应用上下文
 *
 * @author odboy
 * @date 2025-01-12
 */
@Data
@Configuration
public class ElContext {
    public static String applicationName;

    /**
     * 初始化完配置后，加载默认的应用名称
     */
    @Bean
    public BeanFactoryPostProcessor configLoader(ConfigurableEnvironment environment) {
        return configurableListableBeanFactory -> {
            String propertyValue = environment.getProperty("spring.application.name", String.class, ElConstant.APP_NAME);
            if (StrUtil.isBlank(propertyValue)) {
                ElContext.applicationName = ElConstant.APP_NAME;
            } else {
                ElContext.applicationName = propertyValue;
            }
        };
    }

    @Bean
    public SpringBeanHolder springContextHolder() {
        return new SpringBeanHolder();
    }
}
