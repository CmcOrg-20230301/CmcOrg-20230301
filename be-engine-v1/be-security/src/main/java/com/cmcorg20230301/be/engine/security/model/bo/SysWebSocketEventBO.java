package com.cmcorg20230301.be.engine.security.model.bo;

import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import lombok.Data;

import java.util.Set;

/**
 * webSocket事件的 bo
 */
@Data
public class SysWebSocketEventBO<T> {

    /**
     * 用户主键 idSet
     */
    private Set<Long> userIdSet;

    /**
     * socket关联用户主键 idSet
     */
    private Set<Long> sysSocketRefUserIdSet;

    /**
     * 传输的数据
     */
    private WebSocketMessageDTO<T> webSocketMessageDTO;

}
