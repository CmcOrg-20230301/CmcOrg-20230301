package com.cmcorg20230301.engine.be.file.base.controller;

import com.cmcorg20230301.engine.be.file.base.service.SysFileService;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sys/file")
@Tag(name = "文件-管理")
public class SysFileController {

    @Resource
    SysFileService baseService;

    @Operation(summary = "上传文件：共有和私有")
    @PostMapping("/upload")
    public ApiResultVO<String> upload(SysFileUploadDTO dto) {
        return ApiResultVO.ok(baseService.upload(dto));
    }

    @Operation(summary = "下载文件：私有")
    @PostMapping("/privateDownload")
    public void privateDownload(@RequestBody @Valid NotNullId notNullId, HttpServletResponse response) {
        baseService.privateDownload(notNullId, response);
    }

    @Operation(summary = "批量删除文件：共有和私有")
    @PostMapping("/removeByFileIdSet")
    public ApiResultVO<String> removeByFileIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.removeByFileIdSet(notEmptyIdSet));
    }

    @Operation(summary = "批量获取：公开文件的 url")
    @PostMapping("/getPublicUrl")
    public ApiResultVO<LongObjectMapVO<String>> getPublicUrl(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.getPublicUrl(notEmptyIdSet));
    }

}
