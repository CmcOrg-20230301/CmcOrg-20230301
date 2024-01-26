package com.cmcorg20230301.be.engine.sign.phone.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.phone.model.dto.*;
import com.cmcorg20230301.be.engine.sign.phone.service.SignPhoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sign/phone")
@Tag(name = "基础-登录注册-手机")
public class SignPhoneController {

    @Resource
    SignPhoneService baseService;

    @PostMapping(value = "/sign/up/sendCode")
    @Operation(summary = "注册-发送验证码")
    public ApiResultVO<String> signUpSendCode(@RequestBody @Valid PhoneNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.signUpSendCode(dto));
    }

    @PostMapping(value = "/sign/up")
    @Operation(summary = "注册")
    public ApiResultVO<String> signUp(@RequestBody @Valid SignPhoneSignUpDTO dto) {
        return ApiResultVO.okMsg(baseService.signUp(dto));
    }

    @PostMapping(value = "/sign/in/password")
    @Operation(summary = "手机：账号密码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInPassword(@RequestBody @Valid SignPhoneSignInPasswordDTO dto) {
        return ApiResultVO.okData(baseService.signInPassword(dto));
    }

    @PostMapping(value = "/sign/in/sendCode")
    @Operation(summary = "手机验证码登录-发送验证码")
    public ApiResultVO<String> signInSendCode(@RequestBody @Valid PhoneNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.signInSendCode(dto));
    }

    @PostMapping(value = "/sign/in/code")
    @Operation(summary = "手机验证码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInCode(@RequestBody @Valid SignPhoneSignInCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInCode(dto));
    }

    @PostMapping(value = "/setPassword/sendCode")
    @Operation(summary = "设置密码-发送验证码")
    public ApiResultVO<String> setPasswordSendCode() {
        return ApiResultVO.okMsg(baseService.setPasswordSendCode());
    }

    @PostMapping(value = "/setPassword")
    @Operation(summary = "设置密码")
    public ApiResultVO<String> setPassword(@RequestBody @Valid SignPhoneSetPasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.setPassword(dto));
    }

    @PostMapping(value = "/updatePassword/sendCode")
    @Operation(summary = "修改密码-发送验证码")
    public ApiResultVO<String> updatePasswordSendCode() {
        return ApiResultVO.okMsg(baseService.updatePasswordSendCode());
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SignPhoneUpdatePasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/setSignInName/sendCode")
    @Operation(summary = "设置登录名-发送验证码")
    public ApiResultVO<String> setSignInNameSendCode(@RequestBody @Valid SignPhoneSetSignInNameSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInNameSendCode(dto));
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<String> setSignInName(@RequestBody @Valid SignPhoneSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/sendCode")
    @Operation(summary = "修改登录名-发送验证码")
    public ApiResultVO<String> updateSignInNameSendCode(@RequestBody @Valid SignPhoneUpdateSignInNameSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInNameSendCode(dto));
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<String> updateSignInName(@RequestBody @Valid SignPhoneUpdateSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/setEmail/sendCode/phone")
    @Operation(summary = "设置邮箱-发送手机验证码")
    public ApiResultVO<String> setEmailSendCodePhone(@RequestBody @Valid SignPhoneSetEmailSendCodePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmailSendCodePhone(dto));
    }

    @PostMapping(value = "/setEmail/sendCode/email")
    @Operation(summary = "设置邮箱-发送邮箱验证码")
    public ApiResultVO<String> setEmailSendCodeEmail(@RequestBody @Valid SignPhoneSetEmailSendCodeEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmailSendCodeEmail(dto));
    }

    @PostMapping(value = "/setEmail")
    @Operation(summary = "设置邮箱")
    public ApiResultVO<String> setEmail(@RequestBody @Valid SignPhoneSetEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmail(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode/phone")
    @Operation(summary = "修改邮箱-发送手机验证码")
    public ApiResultVO<String> updateEmailSendCodePhone(@RequestBody @Valid SignPhoneUpdateEmailSendCodePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmailSendCodePhone(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode/email")
    @Operation(summary = "修改邮箱-发送邮箱验证码")
    public ApiResultVO<String> updateEmailSendCodeEmail(@RequestBody @Valid SignPhoneUpdateEmailSendCodeEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmailSendCodeEmail(dto));
    }

    @PostMapping(value = "/updateEmail")
    @Operation(summary = "修改邮箱")
    public ApiResultVO<String> updateEmail(@RequestBody @Valid SignPhoneUpdateEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmail(dto));
    }

    @PostMapping(value = "/setWx/sendCode/phone")
    @Operation(summary = "设置微信-发送手机验证码")
    public ApiResultVO<String> setWxSendCodePhone() {
        return ApiResultVO.okMsg(baseService.setWxSendCodePhone());
    }

    @PostMapping(value = "/setWx/getQrCodeUrl")
    @Operation(summary = "设置微信：获取二维码地址")
    public ApiResultVO<GetQrCodeVO> setWxGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setWxGetQrCodeUrl());
    }

    @PostMapping(value = "/setWx/getQrCodeSceneFlag")
    @Operation(summary = "设置微信：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setWxGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setWxGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setWx")
    @Operation(summary = "设置微信")
    public ApiResultVO<SysQrCodeSceneBindVO> setWx(@RequestBody @Valid SignPhoneSetWxDTO dto) {
        return ApiResultVO.okData(baseService.setWx(dto));
    }

    @PostMapping(value = "/updateWx/sendCode/phone")
    @Operation(summary = "修改微信：发送手机验证码")
    public ApiResultVO<String> updateWxSendCodePhone() {
        return ApiResultVO.okMsg(baseService.updateWxSendCodePhone());
    }

    @PostMapping(value = "/updateWx/getQrCodeUrl/new")
    @Operation(summary = "修改微信：获取新微信的二维码地址")
    public ApiResultVO<GetQrCodeVO> updateWxGetQrCodeUrlNew() {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlNew());
    }

    @PostMapping(value = "/updateWx/getQrCodeSceneFlag/new")
    @Operation(summary = "修改微信：获取新微信二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWxGetQrCodeSceneFlagNew(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeSceneFlagNew(notNullId));
    }

    @PostMapping(value = "/updateWx")
    @Operation(summary = "修改微信")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWx(@RequestBody @Valid SignPhoneUpdateWxDTO dto) {
        return ApiResultVO.okData(baseService.updateWx(dto));
    }

    @PostMapping(value = "/updatePhone/sendCode/new")
    @Operation(summary = "修改手机-发送新手机验证码")
    public ApiResultVO<String> updatePhoneSendCodeNew(@RequestBody @Valid SignPhoneUpdatePhoneSendCodeNewDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePhoneSendCodeNew(dto));
    }

    @PostMapping(value = "/updatePhone/sendCode/old")
    @Operation(summary = "修改手机-发送旧手机验证码")
    public ApiResultVO<String> updatePhoneSendCodeOld() {
        return ApiResultVO.okMsg(baseService.updatePhoneSendCodeOld());
    }

    @PostMapping(value = "/updatePhone")
    @Operation(summary = "修改手机")
    public ApiResultVO<String> updatePhone(@RequestBody @Valid SignPhoneUpdatePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/wx/sendCode")
    @Operation(summary = "设置统一登录：微信：发送手机验证码")
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
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignInWxGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setSingleSignInWxGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setSingleSignIn/wx")
    @Operation(summary = "设置统一登录：微信")
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignInWx(@RequestBody @Valid SignPhoneSetSingleSignInWxDTO dto) {
        return ApiResultVO.okData(baseService.setSingleSignInWx(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone/sendCode/current")
    @Operation(summary = "设置统一登录：手机验证码：发送当前账号已经绑定手机的验证码")
    public ApiResultVO<String> setSingleSignInPhoneSendCodeCurrent() {
        return ApiResultVO.okMsg(baseService.setSingleSignInPhoneSendCodeCurrent());
    }

    @PostMapping(value = "/setSingleSignIn/phone/sendCode")
    @Operation(summary = "设置统一登录：手机验证码：发送要绑定统一登录手机的验证码")
    public ApiResultVO<String> setSingleSignInPhoneSendCode(@RequestBody @Valid SignPhoneSetSingleSignInPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInSendCodePhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/phone")
    @Operation(summary = "设置统一登录：手机验证码")
    public ApiResultVO<String> setSingleSignInPhone(@RequestBody @Valid SignPhoneSetSingleSignInPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setSingleSignInPhone(dto));
    }

    @PostMapping(value = "/forgetPassword/sendCode")
    @Operation(summary = "忘记密码-发送验证码")
    public ApiResultVO<String> forgetPasswordSendCode(@RequestBody @Valid PhoneNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.forgetPasswordSendCode(dto));
    }

    @PostMapping(value = "/forgetPassword")
    @Operation(summary = "忘记密码")
    public ApiResultVO<String> forgetPassword(@RequestBody @Valid SignPhoneForgetPasswordDTO dto) {
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
