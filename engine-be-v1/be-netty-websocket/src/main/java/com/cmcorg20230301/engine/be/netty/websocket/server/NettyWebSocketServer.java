package com.cmcorg20230301.engine.be.netty.websocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyWebSocketServer implements CommandLineRunner, DisposableBean {

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void run(String... args) throws Exception {

    }

}
