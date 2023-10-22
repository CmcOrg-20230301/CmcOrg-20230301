package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndStringValue;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.*;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletWithdrawLogDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "用户钱包-提现记录")
@RestController
@RequestMapping("/sys/userWalletWithdrawLog")
public class SysUserWalletWithdrawLogController {

    @Resource
    SysUserWalletWithdrawLogService baseService;

    @Operation(summary = "下拉列表-提现状态")
    @PostMapping("/dictList/withdrawStatus")
    public ApiResultVO<Page<DictIntegerVO>> withdrawStatusDictList() {
        return ApiResultVO.okData(baseService.withdrawStatusDictList());
    }

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysUserWalletWithdrawLogInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "取消")
    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:cancel')")
    public ApiResultVO<String> cancel(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.cancel(notNullId));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:page')")
    public ApiResultVO<Page<SysUserWalletWithdrawLogDO>> myPage(
        @RequestBody @Valid SysUserWalletWithdrawLogPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:infoById')")
    public ApiResultVO<SysUserWalletWithdrawLogDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "分页排序查询-租户")
    @PostMapping("/page/tenant")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:page')")
    public ApiResultVO<Page<SysUserWalletWithdrawLogDO>> myPageTenant(
        @RequestBody @Valid SysUserWalletWithdrawLogPageUserSelfDTO dto) {
        return ApiResultVO.okData(baseService.myPageTenant(dto));
    }

    @Operation(summary = "新增/修改-租户")
    @PostMapping("/insertOrUpdate/tenant")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdateTenant(
        @RequestBody @Valid SysUserWalletWithdrawLogInsertOrUpdateTenantDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdateTenant(dto));
    }

    @Operation(summary = "取消-租户")
    @PostMapping("/cancel/tenant")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:cancel')")
    public ApiResultVO<String> cancelTenant(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.cancelTenant(notNullId));
    }

    @Operation(summary = "分页排序查询-用户")
    @PostMapping("/page/userSelf")
    public ApiResultVO<Page<SysUserWalletWithdrawLogDO>> myPageUserSelf(
        @RequestBody @Valid SysUserWalletWithdrawLogPageUserSelfDTO dto) {
        return ApiResultVO.okData(baseService.myPageUserSelf(dto));
    }

    @Operation(summary = "新增/修改-用户")
    @PostMapping("/insertOrUpdate/userSelf")
    public ApiResultVO<String> insertOrUpdateUserSelf(
        @RequestBody @Valid SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdateUserSelf(dto));
    }

    @Operation(summary = "取消-用户")
    @PostMapping("/cancel/userSelf")
    public ApiResultVO<String> cancelUserSelf(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.cancelUserSelf(notNullId));
    }

    @Operation(summary = "受理-用户的提现记录")
    @PostMapping("/accept")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:accept')")
    public ApiResultVO<String> accept(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.accept(notEmptyIdSet));
    }

    @Operation(summary = "成功-用户的提现记录")
    @PostMapping("/success")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:success')")
    public ApiResultVO<String> success(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.success(notNullId));
    }

    @Operation(summary = "拒绝-用户的提现记录")
    @PostMapping("/reject")
    @PreAuthorize("hasAuthority('sysUserWalletWithdrawLog:reject')")
    public ApiResultVO<String> reject(@RequestBody @Valid NotNullIdAndStringValue notNullIdAndStringValue) {
        return ApiResultVO.okMsg(baseService.reject(notNullIdAndStringValue));
    }

}
