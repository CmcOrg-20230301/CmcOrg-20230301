package com.cmcorg20230301.be.engine.netty.websocket.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface NettyWebSocketController {

}
