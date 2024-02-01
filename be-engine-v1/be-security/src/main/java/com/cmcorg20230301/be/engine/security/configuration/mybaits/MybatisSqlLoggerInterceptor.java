package com.cmcorg20230301.be.engine.security.configuration.mybaits;

///**
// * mybatis-sql日志拦截器
// */
//@Slf4j(topic = LogTopicConstant.MYBATIS)
//@Intercepts({
//        @Signature(type = StatementHandler.class,
//                method = "prepare",
//                args = {Connection.class, Integer.class})})
//public class MybatisSqlLoggerInterceptor implements Interceptor {
public class MybatisSqlLoggerInterceptor {

//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//
//        //通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性;：MetaObject是Mybatis提供的一个用于方便、
//        //优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，同时它支持对JavaBean、Collection、Map三种类型对象的操作。
//        MetaObject metaObject = MetaObject
//                .forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
//                        new DefaultReflectorFactory());
//
//        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
//        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
//
//        //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
//        String id = mappedStatement.getId();
//
//        log.info("id ==> " + id);
//
//        //sql语句类型 select、delete、insert、update
//        String sqlCommandType = mappedStatement.getSqlCommandType().toString();
//
//        log.info("类型 ==> " + sqlCommandType);
//
//        BoundSql boundSql = statementHandler.getBoundSql();
//
//        // 获取节点的配置
//        Configuration configuration = mappedStatement.getConfiguration();
//
//        // 获取到最终的sql语句
//        String newsql = getSql(configuration, boundSql, id);
//
//        log.info("拦截的sql ==>: " + newsql);
//
//        long start = System.currentTimeMillis();
//
//        Object returnValue = invocation.proceed();
//
//        long end = System.currentTimeMillis();
//
//        long time = (end - start);
//
//        log.info("sql耗时 ==>: " + time);
//
//        return returnValue;
//
//    }
//
//    /**
//     * 封装了一下sql语句，
//     * 使得结果返回完整xml路径下的sql语句节点id + sql语句
//     *
//     * @param configuration 配置
//     * @param boundSql      boundSql
//     * @param sqlId         sqlId
//     * @return 返回处理的sql
//     */
//    private String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
//        String sql = showSql(configuration, boundSql);
//        StringBuilder str = new StringBuilder(100);
//        str.append(sqlId);
//        str.append(":");
//        str.append(sql);
//        return str.toString();
//    }
//
//    /**
//     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号；
//     * 对参数是null和不是null的情况作了处理<br>
//     *
//     * @param obj 对象
//     * @return 返回转换后的
//     */
//    private String getParameterValue(Object obj) {
//        String value = null;
//        if (obj instanceof String) {
//            value = "'" + obj.toString() + "'";
//        } else if (obj instanceof Date) {
//            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
//            value = "'" + formatter.format(new Date()) + "'";
//        } else {
//            if (obj != null) {
//                value = obj.toString();
//            } else {
//                value = "";
//            }
//
//        }
//        return value;
//    }
//
//    /**
//     * 进行？的替换
//     *
//     * @param configuration 配置
//     * @param boundSql      原SQL
//     * @return 返回替换后的sql
//     */
//    public String showSql(Configuration configuration, BoundSql boundSql) {
//        // 获取参数
//        Object parameterObject = boundSql.getParameterObject();
//        List<ParameterMapping> parameterMappings = boundSql
//                .getParameterMappings();
//        // sql语句中多个空格都用一个空格代替
//        String sql = boundSql.getSql().replaceAll("[\s]+", " ");
//        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
//            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换　　　　　　　
//            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
//            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
//            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
//                sql = sql.replaceFirst("\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
//
//            } else {
//                //MetaObject主要是封装了originalObject对象，
//                // 提供了get和set的方法用于获取和设置originalObject的属性值,
//                // 主要支持对JavaBean、Collection、Map三种类型对象的操作
//                MetaObject metaObject = configuration.newMetaObject(parameterObject);
//                for (ParameterMapping parameterMapping : parameterMappings) {
//                    String propertyName = parameterMapping.getProperty();
//                    if (metaObject.hasGetter(propertyName)) {
//                        Object obj = metaObject.getValue(propertyName);
//                        sql = sql.replaceFirst("\?", Matcher.quoteReplacement(getParameterValue(obj)));
//                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
//                        // 该分支是动态sql
//                        Object obj = boundSql.getAdditionalParameter(propertyName);
//                        sql = sql.replaceFirst("\?", Matcher.quoteReplacement(getParameterValue(obj)));
//
//                    } else {
//                        //打印出缺失，提醒该参数缺失并防止错位
//                        sql = sql.replaceFirst("\?", "缺失");
//                    }
//                }
//            }
//        }
//        return sql;
//    }

}
