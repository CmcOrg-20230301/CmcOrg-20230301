package com.cmcorg20230301.be.engine.im.session.controller.websocket;

import com.cmcorg20230301.be.engine.im.session.service.impl.SysImSessionContentServiceImpl;
import com.cmcorg20230301.be.engine.netty.websocket.annotation.NettyWebSocketController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@NettyWebSocketController
@RequestMapping(value = "/sys/im/session/content/websocket")
@Tag(name = "基础-即时通讯-会话-内容-websocket-管理")
public class SysImSessionContentWebSocketController {

    @Resource
    SysImSessionContentServiceImpl baseService;

//    @Operation(summary = "用户自我-发送内容-文字")
//    @PostMapping("/send/text/userSelf")
//    public ApiResultVO<NotNullIdAndNotEmptyLongSet> sendTextUserSelf(SysImSessionContentSendTextListDTO dto) {
//        return ApiResultVO.okData(baseService.sendTextUserSelf(dto));
//    }

}
