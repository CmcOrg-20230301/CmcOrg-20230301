package com.cmcorg20230301.be.engine.im.session.controller.websocket;

import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextListDTO;
import com.cmcorg20230301.be.engine.im.session.service.impl.SysImSessionContentServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.netty.websocket.annotation.NettyWebSocketController;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@NettyWebSocketController
@RequestMapping(value = "/sys/im/session/content/webSocket")
@Tag(name = "基础-即时通讯-会话-内容-webSocket-管理")
public class SysImSessionContentWebSocketController {

    @Resource
    SysImSessionContentServiceImpl baseService;

    @Operation(summary = "用户自我-发送内容-文字")
    @PostMapping("/send/text/userSelf")
    public ApiResultVO<NotNullIdAndNotEmptyLongSet> sendTextUserSelf(SysImSessionContentSendTextListDTO dto) {
        return ApiResultVO.okData(baseService.sendTextUserSelf(dto));
    }

}
