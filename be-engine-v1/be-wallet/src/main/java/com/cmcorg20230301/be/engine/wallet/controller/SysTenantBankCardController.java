package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantBankCardService;
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

@Tag(name = "基础-租户银行卡-管理")
@RestController
@RequestMapping("/sys/tenantBankCard")
public class SysTenantBankCardController {

    @Resource
    SysTenantBankCardService baseService;

    @Operation(summary = "新增/修改-租户")
    @PostMapping("/insertOrUpdate/tenant")
    @PreAuthorize("hasAuthority('sysTenantBankCard:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdateTenant(@RequestBody @Valid SysUserBankCardInsertOrUpdateUserSelfDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdateTenant(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysTenantBankCard:page')")
    public ApiResultVO<Page<SysUserBankCardDO>> myPage(@RequestBody @Valid SysUserBankCardPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysTenantBankCard:page')")
    public ApiResultVO<List<SysUserBankCardDO>> tree(@RequestBody @Valid SysUserBankCardPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "通过租户主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysTenantBankCard:infoById')")
    public ApiResultVO<SysUserBankCardDO> infoById(@RequestBody @Valid NotNullLong notNullLong) {
        return ApiResultVO.okData(baseService.infoById(notNullLong));
    }

}
