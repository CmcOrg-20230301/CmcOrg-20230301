package com.cmcorg20230301.engine.be.pay.wx.controller;

import com.cmcorg20230301.engine.be.pay.wx.service.PayWxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/sys/pay/ali")
@Tag(name = "支付-微信")
public class PayWxController {

    @Resource
    PayWxService baseService;

    @Operation(summary = "服务器异步通知，备注：第三方应用调用")
    @PostMapping(value = "/notifyCallBack")
    public String notifyCallBack(HttpServletRequest request) {
        return baseService.notifyCallBack(request);
    }

}
