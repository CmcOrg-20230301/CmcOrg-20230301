package com.cmcorg20230301.be.engine.wallet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogUserSelfPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "用户钱包-操作日志")
@RestController
@RequestMapping("/sys/userWalletLog")
public class SysUserWalletLogController {

    @Resource
    SysUserWalletLogService baseService;

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysUserWalletLog:page')")
    public ApiResultVO<Page<SysUserWalletLogDO>> myPage(@RequestBody @Valid SysUserWalletLogPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "分页排序查询-用户")
    @PostMapping("/page/userSelf")
    public ApiResultVO<Page<SysUserWalletLogDO>> myPageUserSelf(
        @RequestBody @Valid SysUserWalletLogUserSelfPageDTO dto) {
        return ApiResultVO.okData(baseService.myPageUserSelf(dto));
    }

}
