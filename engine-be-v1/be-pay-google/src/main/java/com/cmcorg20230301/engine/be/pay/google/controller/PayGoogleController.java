package com.cmcorg20230301.engine.be.pay.google.controller;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.pay.google.model.dto.SysPayGooglePaySuccessDTO;
import com.cmcorg20230301.engine.be.pay.google.service.PayGoogleService;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sys/payGoogle")
@Tag(name = "支付-谷歌")
public class PayGoogleController {

    @Resource
    PayGoogleService baseService;

    @Operation(summary = "支付成功的回调，备注：由客户端调用")
    @PostMapping(value = "/paySuccess")
    public ApiResultVO<Boolean> paySuccess(@RequestBody @Valid SysPayGooglePaySuccessDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.paySuccess(dto));
    }

    @Operation(summary = "支付核销的回调，备注：由客户端调用")
    @PostMapping(value = "/payConsume")
    public ApiResultVO<Boolean> payConsume(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.payConsume(notNullId));
    }

}
