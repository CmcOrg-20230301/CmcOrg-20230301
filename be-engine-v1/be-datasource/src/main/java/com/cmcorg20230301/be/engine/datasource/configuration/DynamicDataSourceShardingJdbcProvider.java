package com.cmcorg20230301.be.engine.datasource.configuration;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参考：{@link DynamicDataSourceAutoConfiguration}
 */
@Configuration
public class DynamicDataSourceShardingJdbcProvider {

    @Resource
    DynamicDataSourceProperties dynamicDataSourceProperties;

    @Resource
    DefaultDataSourceCreator defaultDataSourceCreator;

    /**
     * 由于本类比 ShardingJdbc，加载更前面，所以要加 Lazy注解，防止注入不进来
     */
    @Lazy
    @Qualifier("shardingSphereDataSource")
    @Resource
    DataSource shardingSphereDataSource;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {

        return new AbstractDataSourceProvider(defaultDataSourceCreator) {

            @Override
            public Map<String, DataSource> loadDataSources() {

                Map<String, DataSource> dataSourceMap = new HashMap<>();

                // 将 shardingJdbc 管理的数据源也交给动态数据源管理，并替换：默认数据源
                dataSourceMap.put(dynamicDataSourceProperties.getPrimary(), shardingSphereDataSource);

                return dataSourceMap;

            }

        };

    }

    /**
     * Primary，注解的目的：因为 ShardingJdbc也会注册一个数据源，所以这里需要加 Primary注解
     * 并且一定要在：DynamicDataSourceAutoConfiguration 之前加载该 Bean
     */
    @Primary
    @Bean
    public DataSource dataSource(List<DynamicDataSourceProvider> providers) {

        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource(providers);

        dynamicRoutingDataSource.setPrimary(dynamicDataSourceProperties.getPrimary());
        dynamicRoutingDataSource.setStrict(dynamicDataSourceProperties.getStrict());
        dynamicRoutingDataSource.setStrategy(dynamicDataSourceProperties.getStrategy());
        dynamicRoutingDataSource.setP6spy(dynamicDataSourceProperties.getP6spy());
        dynamicRoutingDataSource.setSeata(dynamicDataSourceProperties.getSeata());

        return dynamicRoutingDataSource;

    }

}
