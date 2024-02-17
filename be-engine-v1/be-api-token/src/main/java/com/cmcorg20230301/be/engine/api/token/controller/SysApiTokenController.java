package com.cmcorg20230301.be.engine.api.token.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenPageDTO;
import com.cmcorg20230301.be.engine.api.token.model.entity.SysApiTokenDO;
import com.cmcorg20230301.be.engine.api.token.service.SysApiTokenService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "基础-api-token-管理")
@RestController
@RequestMapping("/sys/api/token")
public class SysApiTokenController {

    @Resource
    SysApiTokenService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysApiToken:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysApiTokenInsertOrUpdateDTO dto) {
        return ApiResultVO.okData(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysApiToken:page')")
    public ApiResultVO<Page<SysApiTokenDO>> myPage(@RequestBody @Valid SysApiTokenPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysApiToken:infoById')")
    public ApiResultVO<SysApiTokenDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysApiToken:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

}
