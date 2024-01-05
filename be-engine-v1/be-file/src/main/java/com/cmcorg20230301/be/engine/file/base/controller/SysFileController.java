package com.cmcorg20230301.be.engine.file.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageSelfDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sys/file")
@Tag(name = "基础-文件-管理")
public class SysFileController {

    @Resource
    SysFileService baseService;

    @Operation(summary = "上传文件：公有和私有")
    @PostMapping("/upload")
    public ApiResultVO<Long> upload(SysFileUploadDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.upload(dto));
    }

    @Operation(summary = "下载文件：私有")
    @PostMapping("/privateDownload")
    public void privateDownload(@RequestBody @Valid NotNullId notNullId, HttpServletResponse response) {
        baseService.privateDownload(notNullId, response);
    }

    @Operation(summary = "批量删除文件：公有和私有")
    @PostMapping("/removeByFileIdSet")
    public ApiResultVO<String> removeByFileIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.removeByFileIdSet(notEmptyIdSet, true));
    }

    @Operation(summary = "批量获取：公开文件的 url")
    @PostMapping("/getPublicUrl")
    public ApiResultVO<LongObjectMapVO<String>> getPublicUrl(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okData(baseService.getPublicUrl(notEmptyIdSet));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysFile:page')")
    public ApiResultVO<Page<SysFileDO>> myPage(@RequestBody @Valid SysFilePageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "分页排序查询-自我")
    @PostMapping("/page/self")
    public ApiResultVO<Page<SysFileDO>> myPageSelf(@RequestBody @Valid SysFilePageSelfDTO dto) {
        return ApiResultVO.okData(baseService.myPageSelf(dto));
    }

    @Operation(summary = "分页排序查询-租户")
    @PostMapping("/page/tenant")
    @PreAuthorize("hasAuthority('sysFile:pageTenant')")
    public ApiResultVO<Page<SysFileDO>> myPageTenant(@RequestBody @Valid SysFilePageSelfDTO dto) {
        return ApiResultVO.okData(baseService.myPageTenant(dto));
    }

}
