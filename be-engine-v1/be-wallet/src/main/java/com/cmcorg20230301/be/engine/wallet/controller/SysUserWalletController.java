package com.cmcorg20230301.be.engine.wallet.controller;

import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "钱包-管理")
@RestController
@RequestMapping("/sys/wallet")
public class SysUserWalletController {

    @Resource
    SysUserWalletService baseService;

}
