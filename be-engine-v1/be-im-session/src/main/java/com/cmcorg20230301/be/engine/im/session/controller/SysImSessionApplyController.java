package com.cmcorg20230301.be.engine.im.session.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyUserPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatRejectDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatSelfPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionApplyPrivateChatApplyUserPageVO;
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

    @Operation(summary = "分页排序查询-私聊申请对象列表")
    @PostMapping("/privateChat/apply/user")
    public ApiResultVO<Page<SysImSessionApplyPrivateChatApplyUserPageVO>> privateChatApplyUserPage(@RequestBody @Valid SysImSessionApplyPrivateChatApplyUserPageDTO dto) {
        return ApiResultVO.okData(baseService.privateChatApplyUserPage(dto));
    }

    @Operation(summary = "分页排序查询-私聊申请列表-自我")
    @PostMapping("/privateChat/page/self")
    public ApiResultVO<Page<SysImSessionApplyDO>> privateChatMyPageSelf(@RequestBody @Valid SysImSessionApplyPrivateChatSelfPageDTO dto) {
        return ApiResultVO.okData(baseService.privateChatMyPageSelf(dto));
    }

    @Operation(summary = "私聊：申请添加")
    @PostMapping("/privateChat/apply")
    public ApiResultVO<String> privateChatApply(@RequestBody @Valid SysImSessionApplyPrivateChatApplyDTO dto) {
        return ApiResultVO.okMsg(baseService.privateChatApply(dto));
    }

    @Operation(summary = "私聊：同意添加")
    @PostMapping("/privateChat/agree")
    public ApiResultVO<String> privateChatAgree(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.privateChatAgree(notEmptyIdSet));
    }

    @Operation(summary = "私聊：拒绝添加")
    @PostMapping("/privateChat/reject")
    public ApiResultVO<String> privateChatReject(@RequestBody @Valid SysImSessionApplyPrivateChatRejectDTO dto) {
        return ApiResultVO.okMsg(baseService.privateChatReject(dto));
    }

    @Operation(summary = "私聊：拉黑")
    @PostMapping("/privateChat/block")
    public ApiResultVO<String> privateChatBlock(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.privateChatBlock(notNullId));
    }

    @Operation(summary = "私聊：拉黑取消")
    @PostMapping("/privateChat/block/cancel")
    public ApiResultVO<String> privateChatBlockCancel(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.privateChatBlockCancel(notEmptyIdSet));
    }

    @Operation(summary = "私聊：申请取消")
    @PostMapping("/privateChat/apply/cancel")
    public ApiResultVO<String> privateChatApplyCancel(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.privateChatApplyCancel(notNullId));
    }

    @Operation(summary = "私聊：申请隐藏")
    @PostMapping("/privateChat/apply/hidden")
    public ApiResultVO<String> privateChatApplyHidden(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.privateChatApplyHidden(notNullId));
    }

    @Operation(summary = "私聊：删除")
    @PostMapping("/privateChat/delete")
    public ApiResultVO<String> privateChatDelete(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.privateChatDelete(notNullId));
    }

}
