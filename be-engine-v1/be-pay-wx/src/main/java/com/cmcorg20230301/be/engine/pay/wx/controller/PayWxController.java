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

    @Operation(summary = "服务器异步通知-native，备注：第三方应用调用")
    @PostMapping(value = "/notifyCallBack/native/{tenantId}")
    public void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response,
        @PathVariable Long tenantId) {
        baseService.notifyCallBackNative(request, response, tenantId);
    }

    @Operation(summary = "服务器异步通知-jsApi，备注：第三方应用调用")
    @PostMapping(value = "/notifyCallBack/jsApi/{tenantId}")
    public void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response,
        @PathVariable Long tenantId) {
        baseService.notifyCallBackJsApi(request, response, tenantId);
    }

}
