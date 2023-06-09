package com.cmcorg20230301.engine.be.log.configuration;

import com.cmcorg20230301.engine.be.log.properties.LogProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LogConfiguration {

    @Resource
    public void setLogProperties(LogProperties logProperties) {
        // 备注：不管怎么配置，都可以实时刷新，为什么要这样写，因为 @RefreshScope，会生成一个代理类，配置文件没有相关配置，也会生成代理类，而 打印日志使用的是原来的 LogFilter类，而不是代理类，所以这样写，把这个代理对象给它传递过去
        LogFilter.logProperties = logProperties;
    }

}
