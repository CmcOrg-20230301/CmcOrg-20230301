package com.cmcorg20230301.be.engine.other.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuPageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppOfficialAccountMenuDO;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppOfficialAccountMenuService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/sys/otherApp/officialAccount/menu")
@RestController
@Tag(name = "基础-第三方应用-公众号-菜单-管理")
public class SysOtherAppOfficialAccountMenuController {

    @Resource
    SysOtherAppOfficialAccountMenuService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(
        @RequestBody @Valid SysOtherAppOfficialAccountMenuInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:page')")
    public ApiResultVO<Page<SysOtherAppOfficialAccountMenuDO>> myPage(
        @RequestBody @Valid SysOtherAppOfficialAccountMenuPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:page')")
    public ApiResultVO<List<SysOtherAppOfficialAccountMenuDO>> tree(
        @RequestBody @Valid SysOtherAppOfficialAccountMenuPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:infoById')")
    public ApiResultVO<SysOtherAppOfficialAccountMenuDO> infoById(
        @RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysOtherAppOfficialAccountMenu:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid ChangeNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addOrderNo(dto));
    }

}
