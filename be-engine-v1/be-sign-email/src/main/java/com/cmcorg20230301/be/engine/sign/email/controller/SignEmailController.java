package com.cmcorg20230301.be.engine.sign.email.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;
import com.cmcorg20230301.be.engine.sign.email.service.SignEmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/sign/email")
@Tag(name = "基础-登录注册-邮箱")
public class SignEmailController {

    @Resource
    SignEmailService baseService;

    @PostMapping(value = "/sign/up/sendCode")
    @Operation(summary = "注册-发送验证码")
    public ApiResultVO<String> signUpSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.signUpSendCode(dto));
    }

    @PostMapping(value = "/sign/up")
    @Operation(summary = "注册")
    public ApiResultVO<String> signUp(@RequestBody @Valid SignEmailSignUpDTO dto) {
        return ApiResultVO.okMsg(baseService.signUp(dto));
    }

    @PostMapping(value = "/sign/in/password")
    @Operation(summary = "邮箱：账号密码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInPassword(@RequestBody @Valid SignEmailSignInPasswordDTO dto) {
        return ApiResultVO.okData(baseService.signInPassword(dto));
    }

    @PostMapping(value = "/updatePassword/sendCode")
    @Operation(summary = "修改密码-发送验证码")
    public ApiResultVO<String> updatePasswordSendCode() {
        return ApiResultVO.okMsg(baseService.updatePasswordSendCode());
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SignEmailUpdatePasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/setSignInName/sendCode")
    @Operation(summary = "设置登录名-发送验证码")
    public ApiResultVO<String> setSignInNameSendCode(@RequestBody @Valid SignEmailSetSignInNameSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInNameSendCode(dto));
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<String> setSignInName(@RequestBody @Valid SignEmailSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/sendCode")
    @Operation(summary = "修改登录名-发送验证码")
    public ApiResultVO<String> updateSignInNameSendCode(@RequestBody @Valid SignEmailUpdateSignInNameSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInNameSendCode(dto));
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<String> updateSignInName(@RequestBody @Valid SignEmailUpdateSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode/new")
    @Operation(summary = "修改邮箱-发送新邮箱验证码")
    public ApiResultVO<String> updateEmailSendCodeNew(@RequestBody @Valid SignEmailUpdateEmailSendCodeNewDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmailSendCodeNew(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode/old")
    @Operation(summary = "修改邮箱-发送旧邮箱验证码")
    public ApiResultVO<String> updateEmailSendCodeOld() {
        return ApiResultVO.okMsg(baseService.updateEmailSendCodeOld());
    }

    @PostMapping(value = "/updateEmail")
    @Operation(summary = "修改邮箱")
    public ApiResultVO<String> updateEmail(@RequestBody @Valid SignEmailUpdateEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmail(dto));
    }

    @PostMapping(value = "/setWx/sendCode")
    @Operation(summary = "设置微信：发送验证码")
    public ApiResultVO<String> setWxSendCode() {
        return ApiResultVO.okMsg(baseService.setWxSendCode());
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
    public ApiResultVO<SysQrCodeSceneBindVO> setWx(@RequestBody @Valid SignEmailSetWxDTO dto) {
        return ApiResultVO.okData(baseService.setWx(dto));
    }

    @PostMapping(value = "/setPhone/sendCode/email")
    @Operation(summary = "设置手机：发送邮箱验证码")
    public ApiResultVO<String> setPhoneSendCodeEmail() {
        return ApiResultVO.okMsg(baseService.setPhoneSendCodeEmail());
    }

    @PostMapping(value = "/setPhone/sendCode/phone")
    @Operation(summary = "设置手机：发送手机验证码")
    public ApiResultVO<String> setPhoneSendCodePhone(@RequestBody @Valid SignEmailSetPhoneSendCodePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhoneSendCodePhone(dto));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<String> setPhone(@RequestBody @Valid SignEmailSetPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/wx/sendCode")
    @Operation(summary = "设置统一登录：微信：微信：发送邮箱验证码")
    public ApiResultVO<String> setSingleSignInWxSendCode() {
        return ApiResultVO.okMsg(baseService.setSingleSignInWxSendCode());
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
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignInWx(@RequestBody @Valid SignEmailSetSingleSignInWxDTO dto) {
        return ApiResultVO.okData(baseService.setSingleSignInWx(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone/sendCode/current")
    @Operation(summary = "设置统一登录：手机验证码：发送当前账号已经绑定邮箱的验证码")
    public ApiResultVO<String> setSingleSignInPhoneSendCodeCurrent() {
        return ApiResultVO.okMsg(baseService.setSingleSignInPhoneSendCodeCurrent());
    }

    @PostMapping(value = "/setSingleSignIn/phone/sendCode")
    @Operation(summary = "设置统一登录：手机验证码：发送要绑定统一登录手机的验证码")
    public ApiResultVO<String>
        setSingleSignInPhoneSendCode(@RequestBody @Valid SignEmailSetSingleSignInPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInSendCodePhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone")
    @Operation(summary = "设置统一登录：手机验证码")
    public ApiResultVO<String> setSingleSignInPhone(@RequestBody @Valid SignEmailSetSingleSignInPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInPhone(dto));
    }

    @PostMapping(value = "/forgetPassword/sendCode")
    @Operation(summary = "忘记密码-发送验证码")
    public ApiResultVO<String> forgetPasswordSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.forgetPasswordSendCode(dto));
    }

    @PostMapping(value = "/forgetPassword")
    @Operation(summary = "忘记密码")
    public ApiResultVO<String> forgetPassword(@RequestBody @Valid SignEmailForgetPasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.forgetPassword(dto));
    }

    @PostMapping(value = "/signDelete/sendCode")
    @Operation(summary = "账号注销-发送验证码")
    public ApiResultVO<String> signDeleteSendCode() {
        return ApiResultVO.okMsg(baseService.signDeleteSendCode());
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<String> signDelete(@RequestBody @Valid NotBlankCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.signDelete(dto));
    }

}
