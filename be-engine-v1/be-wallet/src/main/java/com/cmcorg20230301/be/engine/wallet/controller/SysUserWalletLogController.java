package com.cmcorg20230301.be.engine.wallet.controller;

import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户钱包-操作日志")
@RestController
@RequestMapping("/sys/userWalletLog")
public class SysUserWalletLogController {

    @Resource
    SysUserWalletLogService baseService;

}
