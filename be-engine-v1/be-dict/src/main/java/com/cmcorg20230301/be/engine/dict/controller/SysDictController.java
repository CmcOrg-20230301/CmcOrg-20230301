package com.cmcorg20230301.be.engine.dict.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictListByDictKeyDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.be.engine.dict.service.SysDictService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;
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
import java.util.List;

@Tag(name = "字典-管理")
@RestController
@RequestMapping("/sys/dict")
public class SysDictController {

    @Resource
    SysDictService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysDict:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysDictInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysDict:page')")
    public ApiResultVO<Page<SysDictDO>> myPage(@RequestBody @Valid SysDictPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "通过：dictKey获取字典项集合，备注：会进行缓存")
    @PostMapping("/listByDictKey")
    public ApiResultVO<List<DictIntegerVO>> listByDictKey(@RequestBody @Valid SysDictListByDictKeyDTO dto) {
        return ApiResultVO.okData(baseService.listByDictKey(dto));
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysDict:page')")
    public ApiResultVO<List<SysDictDO>> tree(@RequestBody @Valid SysDictPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysDict:infoById')")
    public ApiResultVO<SysDictDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysDict:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet, true));
    }

    @Operation(summary = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysDict:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid ChangeNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addOrderNo(dto));
    }

}
