package com.cmcorg20230301.be.engine.tenant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.*;
import com.cmcorg20230301.be.engine.model.model.vo.DictTreeVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysMenuDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantService;
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

@Tag(name = "租户-管理")
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {

    @Resource
    SysTenantService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysTenant:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysTenantInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysTenant:page')")
    public ApiResultVO<Page<SysTenantDO>> myPage(@RequestBody @Valid SysTenantPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "下拉列表")
    @PostMapping("/dictList")
    @PreAuthorize("hasAuthority('sysTenant:dictList')")
    public ApiResultVO<Page<DictTreeVO>> dictList() {
        return ApiResultVO.okData(baseService.dictList());
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysTenant:page')")
    public ApiResultVO<List<SysTenantDO>> tree(@RequestBody @Valid SysTenantPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysTenant:infoById')")
    public ApiResultVO<SysTenantInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysTenant:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysTenant:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid ChangeNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addOrderNo(dto));
    }

    @Operation(summary = "通过主键id，获取租户名")
    @PostMapping("/getNameById")
    public ApiResultVO<String> getNameById(@RequestBody @Valid NotNullLong notNullLong) {
        return ApiResultVO.okData(baseService.getNameById(notNullLong));
    }

    @Operation(summary = "获取：需要同步给租户的菜单")
    @PostMapping("/getSyncMenuInfo")
    @PreAuthorize("hasAuthority('sysTenant:syncMenu')")
    public ApiResultVO<List<SysMenuDO>> getSyncMenuInfo(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.getSyncMenuInfo(notNullId));
    }

    @Operation(summary = "执行：同步菜单给租户")
    @PostMapping("/doSyncMenu")
    @PreAuthorize("hasAuthority('sysTenant:syncMenu')")
    public ApiResultVO<String> doSyncMenu(@RequestBody @Valid NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet) {
        return ApiResultVO.okMsg(baseService.doSyncMenu(notNullIdAndNotEmptyLongSet));
    }

    @Operation(summary = "执行：同步字典给租户")
    @PostMapping("/doSyncDict")
    @PreAuthorize("hasAuthority('sysTenant:syncDict')")
    public ApiResultVO<String> doSyncDict() {
        return ApiResultVO.okMsg(baseService.doSyncDict());
    }

    @Operation(summary = "执行：同步参数给租户")
    @PostMapping("/doSyncParam")
    @PreAuthorize("hasAuthority('sysTenant:syncParam')")
    public ApiResultVO<String> doSyncParam() {
        return ApiResultVO.okMsg(baseService.doSyncParam());
    }

}
