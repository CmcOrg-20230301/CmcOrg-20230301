package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/im/session/content")
@Tag(name = "基础-即时通讯-会话-内容-管理")
public class SysImSessionContentController {

    @Resource
    SysImSessionContentService baseService;

//    @Operation(summary = "发送内容-文字-用户自我")
//    @PostMapping("/send/text/userSelf")
//    public ApiResultVO<NotNullIdAndNotEmptyLongSet> sendTextUserSelf(@RequestBody @Valid SysImSessionContentSendTextListDTO dto) {
//        return ApiResultVO.okData(baseService.sendTextUserSelf(dto));
//    }
//
//    @Operation(summary = "查询会话内容-用户自我")
//    @PostMapping("/scrollPage/userSelf")
//    public ApiResultVO<Page<SysImSessionContentDO>> scrollPageUserSelf(@RequestBody @Valid SysImSessionContentListDTO dto) {
//        return ApiResultVO.okData(baseService.scrollPageUserSelf(dto));
//    }

}
