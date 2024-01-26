package com.cmcorg20230301.be.engine.sign.single.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysSignConfigurationVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInCodePhoneDTO;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInSendCodePhoneDTO;
import com.cmcorg20230301.be.engine.sign.single.service.SignSingleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sign/single")
@Tag(name = "基础-登录注册-统一")
public class SignSingleController {

    @Resource
    SignSingleService baseService;

    @Operation(summary = "获取：统一登录相关的配置")
    @PostMapping("/sign/in/getConfiguration")
    public ApiResultVO<SysSignConfigurationVO> getSignInConfiguration() {
        return ApiResultVO.okData(baseService.getSignInConfiguration());
    }

    @PostMapping(value = "/sign/in/getQrCodeUrl/wx")
    @Operation(summary = "统一登录：微信扫码登录：获取二维码")
    public ApiResultVO<GetQrCodeVO> signInGetQrCodeUrlWx() {
        return ApiResultVO.okData(baseService.signInGetQrCodeUrlWx(true));
    }

    @PostMapping(value = "/sign/in/byQrCodeId/wx")
    @Operation(summary = "统一登录：微信扫码登录：通过二维码 id", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInByQrCodeIdWx(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signInByQrCodeIdWx(notNullId));
    }

    @PostMapping(value = "/sign/in/sendCode/phone")
    @Operation(summary = "统一登录：手机验证码登录-发送验证码")
    public ApiResultVO<String> signInSendCodePhone(@RequestBody @Valid SignSingleSignInSendCodePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.signInSendCodePhone(dto));
    }

    @PostMapping(value = "/sign/in/code/phone")
    @Operation(summary = "统一登录：手机验证码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInCodePhone(@RequestBody @Valid SignSingleSignInCodePhoneDTO dto) {
        return ApiResultVO.okData(baseService.signInCodePhone(dto));
    }

}
