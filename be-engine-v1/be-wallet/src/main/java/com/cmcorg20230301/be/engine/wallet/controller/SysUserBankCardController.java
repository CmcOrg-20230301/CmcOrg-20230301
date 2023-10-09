package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserBankCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "用户银行卡-管理")
@RestController
@RequestMapping("/sys/userBankCard")
public class SysUserBankCardController {

    @Resource
    SysUserBankCardService baseService;

    @Operation(summary = "新增/修改-用户")
    @PostMapping("/insertOrUpdate/userSelf")
    public ApiResultVO<String> insertOrUpdateUserSelf(
        @RequestBody @Valid SysUserBankCardInsertOrUpdateUserSelfDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdateUserSelf(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUserBankCard:page')")
    public ApiResultVO<Page<SysUserBankCardDO>> myPage(@RequestBody @Valid SysUserBankCardPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysUserBankCard:infoById')")
    public ApiResultVO<SysUserBankCardDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "通过主键id，查看详情-用户")
    @PostMapping("/infoById/userSelf")
    public ApiResultVO<SysUserBankCardDO> infoByIdUserSelf() {
        return ApiResultVO.okData(baseService.infoByIdUserSelf());
    }

}
