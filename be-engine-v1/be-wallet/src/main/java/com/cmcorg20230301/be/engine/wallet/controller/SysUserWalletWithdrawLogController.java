package com.cmcorg20230301.be.engine.wallet.controller;

import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletWithdrawLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "用户钱包-提现记录")
@RestController
@RequestMapping("/sys/userWalletWithdraw")
public class SysUserWalletWithdrawLogController {

    @Resource
    SysUserWalletWithdrawLogService baseService;

}
