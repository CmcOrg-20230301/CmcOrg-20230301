package com.cmcorg20230301.be.engine.other.app.model.interfaces;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxOfficialAccountReceiveMessageDTO;

/**
 * 处理：微信公众号消息
 */
public interface ISysOtherAppWxOfficialAccountReceiveMessageHandle {

    void handle(SysOtherAppWxOfficialAccountReceiveMessageDTO dto);

}
