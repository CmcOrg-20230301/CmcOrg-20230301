package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberIdSetDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "基础-租户钱包-管理")
@RestController
@RequestMapping("/sys/tenantWallet")
public class SysTenantWalletController {

    @Resource
    SysTenantWalletService baseService;

    @Operation(summary = "批量冻结")
    @PostMapping("/frozenByIdSet")
    @PreAuthorize("hasAuthority('sysTenantWallet:frozenByIdSet')")
    public ApiResultVO<String> frozenByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okData(baseService.frozenByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "批量解冻")
    @PostMapping("/thawByIdSet")
    @PreAuthorize("hasAuthority('sysTenantWallet:thawByIdSet')")
    public ApiResultVO<String> thawByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okData(baseService.thawByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysTenantWallet:page')")
    public ApiResultVO<Page<SysUserWalletDO>> myPage(@RequestBody @Valid SysUserWalletPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysTenantWallet:page')")
    public ApiResultVO<List<SysUserWalletDO>> tree(@RequestBody @Valid SysUserWalletPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "通过租户主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysTenantWallet:infoById')")
    public ApiResultVO<SysUserWalletDO> infoById(@RequestBody @Valid NotNullLong notNullLong) {
        return ApiResultVO.okData(baseService.infoById(notNullLong));
    }

    @Operation(summary = "通过租户主键 idSet，加减可提现的钱")
    @PostMapping("/addWithdrawableMoney/background")
    @PreAuthorize("hasAuthority('sysTenantWallet:addWithdrawableMoney:background')")
    public ApiResultVO<String> addWithdrawableMoneyBackground(
        @RequestBody @Valid ChangeBigDecimalNumberIdSetDTO dto) {
        return ApiResultVO.okMsg(baseService.addWithdrawableMoneyBackground(dto));
    }

}
