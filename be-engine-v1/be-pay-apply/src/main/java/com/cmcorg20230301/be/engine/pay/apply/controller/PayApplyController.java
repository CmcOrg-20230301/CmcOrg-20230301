package com.cmcorg20230301.be.engine.pay.apply.controller;

import cn.hutool.json.JSONObject;
import com.cmcorg20230301.be.engine.pay.apply.service.PayApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/sys/payApply")
@Tag(name = "支付-苹果")
public class PayApplyController {

    @Resource
    PayApplyService baseService;

    @Operation(summary = "服务器异步通知，备注：第三方应用调用", hidden = true)
    @PostMapping(value = "/notifyCallBack")
    public String notifyCallBack(@RequestBody JSONObject jsonObject) {
        return baseService.notifyCallBack(jsonObject);
    }

}
