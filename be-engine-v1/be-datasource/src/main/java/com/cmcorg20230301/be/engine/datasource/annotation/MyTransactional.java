package com.cmcorg20230301.be.engine.datasource.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * 这个 {@link com.baomidou.dynamic.datasource.annotation.DSTransactional} 注解的含义是：多数据源事务，既：多个事务循环提交，或者多个事务同时回滚
 * <p>
 * <p>
 * 建议：全部使用：@DSTransactional 注解，避免不必要的麻烦
 * 不建议：@DSTransactional 和 @Transactional 嵌套混用，最好的是 只用一个注解
 * 也不建议：多个 @DS 和 @Transactional 嵌套混用，会出现，切换不了数据源的问题，最好的是 @DS 和 @ Transactional，一对一
 * <p>
 * <p>
 * 备注：@DS，在 @PostConstruct 注解, InitializingBean接口, 自定义init-method，里面使用会无效，因为不会进入 aop
 * 备注：@DS，在一个方法调用另外一个方法的时候，也不会生效，因为不会进入 aop
 * 备注：但是可以手动切换数据源：DynamicDataSourceContextHolder.push("slave"); 但是使用完毕之后，要 DynamicDataSourceContextHolder.clear()，不然该线程还会有这个数据源
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Transactional(rollbackFor = Exception.class)
public @interface MyTransactional {
}
