package com.cmcorg20230301.engine.be.param.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.param.model.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.param.model.dto.SysParamPageDTO;
import com.cmcorg20230301.engine.be.param.service.SysParamService;
import com.cmcorg20230301.engine.be.security.model.entity.SysParamDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RequestMapping("/sys/param")
@RestController
@Tag(name = "系统参数-管理")
public class SysParamController {

    @Resource
    SysParamService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysParam:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysParamInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest() {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest2")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest2(@RequestParam(value = "file") MultipartFile file) {
    }

    @Data
    public static class MyPageTest3DTO {

        private MultipartFile file;

        private Long fileBegin;

        private Long fileEnd;

    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest3")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest3(MyPageTest3DTO dto) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest4")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest4(@RequestParam(value = "file") MultipartFile file,
        @RequestParam(value = "fileName") String fileName, @RequestParam(value = "uploadType") Integer uploadType) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest5")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest5(@RequestBody String test) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest6")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest6(JSONObject jsonObject) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest7")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest7(@RequestBody JSONObject jsonObject) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest8")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest8(Map<String, Object> map) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/pageTest9")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public void myPageTest9(@RequestBody Map<String, Object> map) {
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysParam:page')")
    public ApiResultVO<Page<SysParamDO>> myPage(@RequestBody @Valid SysParamPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysParam:infoById')")
    public ApiResultVO<SysParamDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysParam:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

}
