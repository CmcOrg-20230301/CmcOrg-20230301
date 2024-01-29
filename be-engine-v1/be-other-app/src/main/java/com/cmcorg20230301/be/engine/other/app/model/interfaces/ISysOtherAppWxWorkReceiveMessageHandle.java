package com.cmcorg20230301.be.engine.other.app.model.interfaces;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxWorkReceiveMessageDTO;

/**
 * 处理：企业微信消息
 */
public interface ISysOtherAppWxWorkReceiveMessageHandle {

    void handle(SysOtherAppWxWorkReceiveMessageDTO dto);

}
