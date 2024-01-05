package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.model.model.vo.DictStringVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateDTO;
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

@Tag(name = "基础-用户银行卡-管理")
@RestController
@RequestMapping("/sys/userBankCard")
public class SysUserBankCardController {

    @Resource
    SysUserBankCardService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysUserBankCard:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysUserBankCardInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

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

    @Operation(summary = "下拉列表-开户行名称")
    @PostMapping("/dictList/openBankName")
    public ApiResultVO<Page<DictStringVO>> openBankNameDictList() {
        return ApiResultVO.okData(baseService.openBankNameDictList());
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysUserBankCard:infoById')")
    public ApiResultVO<SysUserBankCardDO> infoById(@RequestBody @Valid NotNullLong notNullLong) {
        return ApiResultVO.okData(baseService.infoById(notNullLong));
    }

    @Operation(summary = "通过主键id，查看详情-用户")
    @PostMapping("/infoById/userSelf")
    public ApiResultVO<SysUserBankCardDO> infoByIdUserSelf() {
        return ApiResultVO.okData(baseService.infoByIdUserSelf());
    }

}
