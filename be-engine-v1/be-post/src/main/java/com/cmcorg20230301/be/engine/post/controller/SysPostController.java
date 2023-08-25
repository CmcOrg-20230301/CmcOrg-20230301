package com.cmcorg20230301.be.engine.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostPageDTO;
import com.cmcorg20230301.be.engine.post.model.entity.SysPostDO;
import com.cmcorg20230301.be.engine.post.model.vo.SysPostInfoByIdVO;
import com.cmcorg20230301.be.engine.post.service.SysPostService;
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

@Tag(name = "岗位-管理")
@RestController
@RequestMapping("/sys/post")
public class SysPostController {

    @Resource
    SysPostService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysPost:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysPostInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysPost:page')")
    public ApiResultVO<Page<SysPostDO>> myPage(@RequestBody @Valid SysPostPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('sysPost:page')")
    public ApiResultVO<List<SysPostDO>> tree(@RequestBody @Valid SysPostPageDTO dto) {
        return ApiResultVO.okData(baseService.tree(dto));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysPost:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysPost:infoById')")
    public ApiResultVO<SysPostInfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.infoById(notNullId));
    }

    @Operation(summary = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('sysPost:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid ChangeNumberDTO dto) {
        return ApiResultVO.okMsg(baseService.addOrderNo(dto));
    }

}
