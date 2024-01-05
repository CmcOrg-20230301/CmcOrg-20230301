package com.cmcorg20230301.be.engine.pay.base.controller;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RequestMapping("/sys/pay")
@RestController
@Tag(name = "基础-支付-管理")
public class SysPayController {

    @Resource
    SysPayService baseService;

    @Operation(summary = "通过主键id，查看支付状态-本平台")
    @PostMapping("/payTradeStatusById")
    public ApiResultVO<SysPayTradeStatusEnum> payTradeStatusById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.payTradeStatusById(notNullId));
    }

    @Operation(summary = "通过主键id，查看支付状态-第三方支付平台")
    @PostMapping("/payTradeStatusById/other")
    public ApiResultVO<SysPayTradeStatusEnum> payTradeStatusByIdOther(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.payTradeStatusByIdOther(notNullId));
    }

}
