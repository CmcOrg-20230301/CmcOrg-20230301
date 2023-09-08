package com.cmcorg20230301.be.engine.datasource.configuration;

//@Configuration
public class MyDataSourceConfiguration {

    //    @Resource
    //    DynamicDataSourceProperties dynamicDataSourceProperties;

    //    @Resource
    //    DynamicRoutingDataSource dynamicRoutingDataSource;

    //    /**
    //     * @Lazy：这个注解必须加，表示可以延迟加载这个 bean，目前的顺序是：先加载 动态数据源，再加载 shardingJdbc的数据源，即本数据源
    //     */
    //    @Lazy
    //    @Resource
    //    AbstractDataSourceAdapter abstractDataSourceAdapter;
    //
    //    @Bean
    //    public DynamicDataSourceProvider shardingJdbcDynamicDataSourceProvider() {
    //
    //        return new DynamicDataSourceProvider() {
    //
    //            @Override
    //            public Map<String, DataSource> loadDataSources() {
    //
    //                Map<String, DataSource> dataSourceMap = MapUtil.newHashMap();
    //
    //                // 将 shardingJdbc 管理的数据源也交给动态数据源管理，并替换：默认数据源
    //                dataSourceMap.put(dynamicDataSourceProperties.getPrimary(), abstractDataSourceAdapter);
    //
    //                return dataSourceMap;
    //
    //            }
    //
    //        };
    //
    //    }
    //
    //    @Primary
    //    @Bean
    //    public DataSource primaryDataSource() {
    //        return dynamicRoutingDataSource;
    //    }

}
