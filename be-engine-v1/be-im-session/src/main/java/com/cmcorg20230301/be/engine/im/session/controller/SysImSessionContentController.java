package com.cmcorg20230301.be.engine.im.session.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/sys/im/session/content")
@Tag(name = "基础-即时通讯-会话-内容-管理")
public class SysImSessionContentController {

    @Resource
    SysImSessionContentService baseService;

    @Operation(summary = "发送内容-文字-用户自我")
    @PostMapping("/send/text/userSelf")
    public ApiResultVO<Set<Long>> sendTextUserSelf(@RequestBody @Valid SysImSessionContentSendTextListDTO dto) {
        return ApiResultVO.okData(baseService.sendTextUserSelf(dto));
    }

    @Operation(summary = "查询会话内容-用户自我")
    @PostMapping("/scrollPage/userSelf")
    public ApiResultVO<Page<SysImSessionContentDO>> scrollPageUserSelf(@RequestBody @Valid SysImSessionContentListDTO dto) {
        return ApiResultVO.okData(baseService.scrollPageUserSelf(dto));
    }

}
