package com.cmcorg20230301.be.engine.sign.signinname.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;
import com.cmcorg20230301.be.engine.sign.signinname.service.SignSignInNameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/sign/signInName")
@Tag(name = "基础-登录注册-登录名")
public class SignSignInNameController {

    @Resource
    SignSignInNameService baseService;

    @PostMapping(value = "/sign/up")
    @Operation(summary = "注册")
    public ApiResultVO<String> signUp(@RequestBody @Valid SignSignInNameSignUpDTO dto) {
        return ApiResultVO.okMsg(baseService.signUp(dto));
    }

    @PostMapping(value = "/sign/in/password")
    @Operation(summary = "账号密码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInPassword(@RequestBody @Valid SignSignInNameSignInPasswordDTO dto) {
        return ApiResultVO.okData(baseService.signInPassword(dto));
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SignSignInNameUpdatePasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<String> updateSignInName(@RequestBody @Valid SignSignInNameUpdateSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/setEmail/sendCode")
    @Operation(summary = "设置邮箱：发送验证码")
    public ApiResultVO<String> setEmailSendCode(@RequestBody @Valid SignSignInNameSetEmailSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmailSendCode(dto));
    }

    @PostMapping(value = "/setEmail")
    @Operation(summary = "设置邮箱")
    public ApiResultVO<String> setEmail(@RequestBody @Valid SignSignInNameSetEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmail(dto));
    }

    @PostMapping(value = "/setWx/getQrCodeUrl")
    @Operation(summary = "设置微信：获取二维码地址")
    public ApiResultVO<GetQrCodeVO> setWxGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setWxGetQrCodeUrl());
    }

    @PostMapping(value = "/setWx/getQrCodeSceneFlag")
    @Operation(summary = "设置微信：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> getQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.getQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setWx")
    @Operation(summary = "设置微信")
    public ApiResultVO<SysQrCodeSceneBindVO> setWx(@RequestBody @Valid SignSignInNameSetWxDTO dto) {
        return ApiResultVO.okData(baseService.setWx(dto));
    }

    @PostMapping(value = "/setPhone/sendCode")
    @Operation(summary = "设置手机：发送验证码")
    public ApiResultVO<String> setPhoneSendCode(@RequestBody @Valid SignSignInNameSetPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhoneSendCode(dto));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<String> setPhone(@RequestBody @Valid SignSignInNameSetPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/wx/getQrCodeUrl")
    @Operation(summary = "设置统一登录：微信：获取统一登录微信的二维码地址")
    public ApiResultVO<GetQrCodeVO> setSingleSignInWxGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setSingleSignInWxGetQrCodeUrl());
    }

    @PostMapping(value = "/setSingleSignIn/wx/getQrCodeSceneFlag")
    @Operation(summary = "设置统一登录：微信：获取统一登录微信的二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO>
        setSingleSignInWxGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setSingleSignInWxGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setSingleSignIn/wx")
    @Operation(summary = "设置统一登录：微信")
    public ApiResultVO<SysQrCodeSceneBindVO>
        setSingleSignInWx(@RequestBody @Valid SignSignInNameSetSingleSignInWxDTO dto) {
        return ApiResultVO.okData(baseService.setSingleSignInWx(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone/sendCode")
    @Operation(summary = "设置统一登录：手机验证码：发送验证码")
    public ApiResultVO<String>
        setSingleSignInPhoneSendCode(@RequestBody @Valid SignSignInNameSetSingleSignInPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInSendCodePhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone")
    @Operation(summary = "设置统一登录：手机验证码")
    public ApiResultVO<String> setSingleSignInPhone(@RequestBody @Valid SignSignInNameSetSingleSignInPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInPhone(dto));
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<String> signDelete(@RequestBody @Valid SignSignInNameSignDeleteDTO dto) {
        return ApiResultVO.okMsg(baseService.signDelete(dto));
    }

}
