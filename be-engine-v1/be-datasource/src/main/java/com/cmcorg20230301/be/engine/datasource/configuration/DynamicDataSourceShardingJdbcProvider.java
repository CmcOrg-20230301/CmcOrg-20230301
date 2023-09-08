package com.cmcorg20230301.be.engine.datasource.configuration;

import cn.hutool.core.map.MapUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.*;
import org.apache.shardingsphere.driver.jdbc.adapter.AbstractDataSourceAdapter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 参考：{@link DynamicDataSourceAutoConfiguration}
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(value = DynamicDataSourceAutoConfiguration.class)
@Import({DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class,
    DynamicDataSourceAopConfiguration.class, DynamicDataSourceAssistConfiguration.class})
public class DynamicDataSourceShardingJdbcProvider implements DynamicDataSourceProvider {

    @Resource
    DynamicDataSourceProperties dynamicDataSourceProperties;

    /**
     * 由于本类比 ShardingJdbc，加载更前面，所以要加 Lazy注解，防止注入不进来
     */
    @Lazy
    @Resource
    AbstractDataSourceAdapter abstractDataSourceAdapter;

    @Override
    public Map<String, DataSource> loadDataSources() {

        Map<String, DataSource> dataSourceMap = MapUtil.newHashMap();

        // 将 shardingJdbc 管理的数据源也交给动态数据源管理，并替换：默认数据源
        dataSourceMap.put(dynamicDataSourceProperties.getPrimary(), abstractDataSourceAdapter);

        return dataSourceMap;

    }

    /**
     * Primary，注解的目的：因为 ShardingJdbc也会注册一个数据源，所以这里需要加 Primary注解
     * 并且一定要在：DynamicDataSourceAutoConfiguration 之前加载该 Bean，并且 Bean的名字不能相同，不然会报错
     */
    @Primary
    @Bean
    public DataSource myDataSource() {

        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        dynamicRoutingDataSource.setPrimary(dynamicDataSourceProperties.getPrimary());
        dynamicRoutingDataSource.setStrict(dynamicDataSourceProperties.getStrict());
        dynamicRoutingDataSource.setStrategy(dynamicDataSourceProperties.getStrategy());
        dynamicRoutingDataSource.setP6spy(dynamicDataSourceProperties.getP6spy());
        dynamicRoutingDataSource.setSeata(dynamicDataSourceProperties.getSeata());

        return dynamicRoutingDataSource;

    }

}
