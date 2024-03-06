package com.cmcorg20230301.be.engine.sms.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationPageDTO;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.service.SysSmsConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RequestMapping("/sys/smsConfiguration")
@RestController
@Tag(name = "基础-短信配置-管理")
public class SysSmsConfigurationController {

    @Resource
    SysSmsConfigurationService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysSmsConfiguration:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(
        @RequestBody @Valid SysSmsConfigurationInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysSmsConfiguration:page')")
    public ApiResultVO<Page<SysSmsConfigurationDO>> myPage(
        @RequestBody @Valid SysSmsConfigurationPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysSmsConfiguration:infoById')")
    public ApiResultVO<SysSmsConfigurationDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysSmsConfiguration:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

}
