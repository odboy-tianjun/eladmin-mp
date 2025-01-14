package cn.odboy.infra.database;

import cn.odboy.infra.mybatisplus.MyMetaObjectHandler;
import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 配置数据源
 *
 * @author odboy
 * @date 2025-01-15
 */
@Slf4j
@Configuration
@MapperScan(
        basePackages = {
                "cn.odboy.modules.devops.mapper",
        },
        sqlSessionFactoryRef = "sqlSystemSessionFactoryDevops"
)
public class DataSourceConfigOfDevops {
    @Value("${spring.profiles.active}")
    private String activeEnv;
    @Value("${spring.datasource.devops.url}")
    private String url;
    @Value("${spring.datasource.devops.username}")
    private String username;
    @Value("${spring.datasource.devops.password}")
    private String password;
    @Autowired
    private MybatisPlusInterceptor interceptor;
    @Autowired
    private MyMetaObjectHandler metaObjectHandler;

    @Bean
    public DataSource dataSourceDevops() {
        DruidDataSource dataSource = new DruidDataSource();
        if (DataSourceConst.Dev.equals(activeEnv)) {
            dataSource.setDriverClassName(DataSourceConst.SpyDriverClassName);
        } else {
            dataSource.setDriverClassName(DataSourceConst.DriverClassName);
        }
        dataSource.setDbType(DbType.mysql);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 初始连接数
        dataSource.setInitialSize(5);
        // 最小连接数
        dataSource.setMinIdle(15);
        // 最大连接数
        dataSource.setMaxActive(30);
        // 超时时间(以秒数为单位)
        dataSource.setRemoveAbandonedTimeout(100);
        // 获取连接超时时间
        dataSource.setMaxWait(3000);
        // 连接有效性检测时间
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        // 连接在池中最小生存的时间
        dataSource.setMinEvictableIdleTimeMillis(300000);
        // 连接在池中最大生存的时间
        dataSource.setMaxEvictableIdleTimeMillis(900000);
        // 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除
        dataSource.setTestWhileIdle(true);
        // 指明是否在从池中取出连接前进行检验,如果检验失败, 则从池中去除连接并尝试取出另一个
        dataSource.setTestOnBorrow(true);
        // 是否在归还到池中前进行检验
        dataSource.setTestOnReturn(false);
        // 检测连接是否有效
        dataSource.setValidationQuery("select 1");
        // 配置监控统计
        try {
            dataSource.setFilters("stat,wall,log4j");
            dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=1000");
        } catch (SQLException e) {
            log.warn("配置数据源监控失败", e);
        }
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSystemSessionFactoryDevops() throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        MybatisConfiguration configuration = new MybatisConfiguration();
        // 开启 Mybatis 二级缓存，默认为 true
        configuration.setCacheEnabled(false);
        // 设置本地缓存作用域, Mybatis 一级缓存, 默认为 SESSION
        // 同一个 session 相同查询语句不会再次查询数据库
        // 微服务中, 建议设置为STATEMENT, 即关闭一级缓存
        configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
        // 是否开启自动驼峰命名规则（camel case）映射, 即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);
        // 配置数据源
        factoryBean.setDataSource(dataSourceDevops());
        // MyBatis Mapper 所对应的 XML 文件位置
        // Maven 多模块项目的扫描路径需以 classpath*: 开头 （即加载多个 jar 包下的 XML 文件）
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));
        // MyBatis-Plus 全局策略中的 DB 策略配置
        GlobalConfig globalConfig = new GlobalConfig();
        // 是否控制台 print mybatis-plus 的 LOGO
        globalConfig.setBanner(true);
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 全局默认主键类型, 这里为自增主键
        dbConfig.setIdType(IdType.AUTO);
        dbConfig.setLogicDeleteField("available");
        // 逻辑未删除值(逻辑删除下有效)
        dbConfig.setLogicNotDeleteValue("1");
        // 逻辑已删除值(逻辑删除下有效)
        dbConfig.setLogicDeleteValue("0");
        // 表名是否使用驼峰转下划线命名,只对表名生效
        dbConfig.setTableUnderline(true);
        globalConfig.setDbConfig(dbConfig);
        factoryBean.setGlobalConfig(globalConfig);
        // 配置插件
        factoryBean.setPlugins(interceptor);
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplateDevops() throws Exception {
        return new SqlSessionTemplate(sqlSystemSessionFactoryDevops());
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManagerDevops() {
        return new DataSourceTransactionManager(dataSourceDevops());
    }
}
