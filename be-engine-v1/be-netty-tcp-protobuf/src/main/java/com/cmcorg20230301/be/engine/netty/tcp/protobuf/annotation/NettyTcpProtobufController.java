package com.cmcorg20230301.be.engine.netty.tcp.protobuf.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface NettyTcpProtobufController {

}
