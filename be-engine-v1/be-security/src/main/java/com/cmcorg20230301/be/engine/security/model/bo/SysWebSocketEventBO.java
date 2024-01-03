package com.cmcorg20230301.be.engine.security.model.bo;

import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import lombok.Data;

import java.util.Set;

/**
 * webSocket事件的 bo
 */
@Data
public class SysWebSocketEventBO {

    /**
     * 用户主键 idSet
     */
    private Set<Long> userIdSet;

    /**
     * 传输的数据
     */
    private WebSocketMessageDTO<?> dto;

}
