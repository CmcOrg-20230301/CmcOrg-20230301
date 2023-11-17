package com.cmcorg20230301.be.engine.pay.wx.controller;

import com.cmcorg20230301.be.engine.pay.wx.service.PayWxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/sys/payWx")
@Tag(name = "支付-微信")
public class PayWxController {

    @Resource
    PayWxService baseService;

    @Operation(summary = "服务器异步通知-native，备注：第三方应用调用", hidden = true)
    @PostMapping(value = "/notifyCallBack/native/{sysPayConfigurationId}")
    public void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response,
        @PathVariable(value = "sysPayConfigurationId") long sysPayConfigurationId) {
        baseService.notifyCallBackNative(request, response, sysPayConfigurationId);
    }

    @Operation(summary = "服务器异步通知-jsApi，备注：第三方应用调用", hidden = true)
    @PostMapping(value = "/notifyCallBack/jsApi/{sysPayConfigurationId}")
    public void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response,
        @PathVariable(value = "sysPayConfigurationId") long sysPayConfigurationId) {
        baseService.notifyCallBackJsApi(request, response, sysPayConfigurationId);
    }

}
