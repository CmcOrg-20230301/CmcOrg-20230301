package com.cmcorg20230301.be.engine.im.session.controller.websocket;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.netty.websocket.annotation.NettyWebSocketController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@NettyWebSocketController
@RequestMapping(value = "/sys/im/session/refUser/websocket")
@Tag(name = "基础-即时通讯-会话-用户-websocket-管理")
public class SysImSessionRefUserWebSocketController {

    @Resource
    SysImSessionRefUserService baseService;

//    @Operation(summary = "更新-最后一次打开会话的时间戳-用户自我")
//    @PostMapping("/update/lastOpenTs/userSelf")
//    public ApiResultVO<String> updateLastOpenTsUserSelf(@Valid NotNullId notNullId) {
//        return ApiResultVO.okMsg(baseService.updateLastOpenTsUserSelf(notNullId));
//    }

}
