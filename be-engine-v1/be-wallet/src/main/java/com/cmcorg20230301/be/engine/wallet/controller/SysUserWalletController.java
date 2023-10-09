package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "用户钱包-管理")
@RestController
@RequestMapping("/sys/userWallet")
public class SysUserWalletController {

    @Resource
    SysUserWalletService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysUserWallet:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysUserWalletInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUserWallet:page')")
    public ApiResultVO<Page<SysUserWalletDO>> myPage(@RequestBody @Valid SysUserWalletPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysUserWallet:infoById')")
    public ApiResultVO<SysUserWalletDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "通过主键id，查看详情-用户")
    @PostMapping("/infoById/userSelf")
    public ApiResultVO<SysUserWalletDO> infoByIdUserSelf() {
        return ApiResultVO.okData(baseService.infoByIdUserSelf());
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysUserWallet:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "通过主键 idSet，加减总金额")
    @PostMapping("/addTotalMoney/background")
    @PreAuthorize("hasAuthority('sysUserWallet:addTotalMoney')")
    public ApiResultVO<String> addTotalMoneyBackground(@RequestBody @Valid ChangeBigDecimalNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addTotalMoneyBackground(dto));
    }

    @Operation(summary = "通过主键 idSet，加减可提现的钱")
    @PostMapping("/addWithdrawableMoney/background")
    @PreAuthorize("hasAuthority('sysUserWallet:addWithdrawableMoney')")
    public ApiResultVO<String> addWithdrawableMoneyBackground(@RequestBody @Valid ChangeBigDecimalNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addWithdrawableMoneyBackground(dto));
    }

}
