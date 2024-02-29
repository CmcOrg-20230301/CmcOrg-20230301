package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionApplyService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/sys/im/session/apply")
@Tag(name = "基础-即时通讯-会话-申请-管理")
public class SysImSessionApplyController {

    @Resource
    SysImSessionApplyService baseService;

    @Operation(summary = "私聊：申请添加")
    @PostMapping("/privateChat/apply")
    public ApiResultVO<String> privateChatApply(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.privateChatApply(notNullId));
    }

    @Operation(summary = "私聊：同意添加")
    @PostMapping("/privateChat/agree")
    public ApiResultVO<String> privateChatAgree(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.privateChatAgree(notEmptyIdSet));
    }

}
