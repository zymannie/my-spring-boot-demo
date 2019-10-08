package com.annie.db.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.annie.db.mapper",sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class DbConfig {

    @Value(value = "classpath:mybatis/mybatis-config.xml")
    private Resource configLocation;

    @Value(value = "classpath:mybatis/mapper/*.xml")
    private Resource[] mapperLocations;

    @Bean(initMethod = "init", destroyMethod = "close",name="dbDataSource")
    public DruidDataSource dataSource(@Value("${spring.datasource.url}") String url,
                                      @Value("${spring.datasource.username}") String username,
                                      @Value("${spring.datasource.password}") String password) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(60);
        druidDataSource.setInitialSize(15);
        druidDataSource.setMaxWait(60000);//60s
        druidDataSource.setMinIdle(1);
        druidDataSource.setTimeBetweenEvictionRunsMillis(6000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        druidDataSource.setFilters("config");
        Properties properties = new Properties();
        properties.put("config.decrypt", "false");
        druidDataSource.setConnectProperties(properties);

        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(10000);//10s。。慢
        statFilter.setMergeSql(true);
        statFilter.setLogSlowSql(true);

        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(statFilter);
        druidDataSource.setProxyFilters(filterList);

        return druidDataSource;
    }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory hsSqlSessionFactory(@Qualifier("dbDataSource") DruidDataSource druidDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(druidDataSource);
        sessionFactory.setMapperLocations(mapperLocations);
        sessionFactory.setConfigLocation(configLocation);
        return sessionFactory.getObject();
    }
}