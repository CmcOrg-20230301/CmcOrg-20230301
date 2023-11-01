package com.cmcorg20230301.be.engine.other.app.model.interfaces;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxReceiveMessageDTO;

/**
 * 处理：微信公众号消息
 */
public interface ISysOtherAppOfficialAccountWxReceiveMessageHandle {

    void handle(SysOtherAppOfficialAccountWxReceiveMessageDTO dto);

}
