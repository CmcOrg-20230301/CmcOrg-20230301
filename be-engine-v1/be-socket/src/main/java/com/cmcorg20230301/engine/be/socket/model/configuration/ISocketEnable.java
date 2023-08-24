package com.cmcorg20230301.engine.be.socket.model.configuration;

import java.util.Set;

public interface ISocketEnable {

    /**
     * 执行处理
     */
    void handle(Set<Long> socketIdSet);

}
