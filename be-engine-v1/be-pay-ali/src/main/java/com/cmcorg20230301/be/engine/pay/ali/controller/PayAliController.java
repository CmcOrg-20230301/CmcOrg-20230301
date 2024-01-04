package com.cmcorg20230301.be.engine.pay.ali.controller;

import com.cmcorg20230301.be.engine.pay.ali.service.PayAliService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/sys/payAli")
@Tag(name = "支付-支付宝")
public class PayAliController {

    @Resource
    PayAliService baseService;

    @Operation(summary = "服务器异步通知，备注：第三方应用调用", hidden = true)
    @PostMapping(value = "/notifyCallBack/{sysPayConfigurationId}")
    public String notifyCallBack(HttpServletRequest request,
                                 @PathVariable(value = "sysPayConfigurationId") long sysPayConfigurationId) {
        return baseService.notifyCallBack(request, sysPayConfigurationId);
    }

}
