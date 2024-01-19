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
    public ApiResultVO<String> setSignInNameSendCode() {
        return ApiResultVO.okMsg(baseService.setSignInNameSendCode());
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<String> setSignInName(@RequestBody @Valid SignPhoneSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/sendCode")
    @Operation(summary = "修改登录名-发送验证码")
    public ApiResultVO<String> updateSignInNameSendCode() {
        return ApiResultVO.okMsg(baseService.updateSignInNameSendCode());
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<String> updateSignInName(@RequestBody @Valid SignPhoneSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/setEmail/sendCode")
    @Operation(summary = "设置邮箱-发送验证码")
    public ApiResultVO<String> setEmailSendCode() {
        return ApiResultVO.okMsg(baseService.setEmailSendCode());
    }

    @PostMapping(value = "/setEmail")
    @Operation(summary = "设置邮箱")
    public ApiResultVO<String> setEmail(@RequestBody @Valid SignPhoneSetEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmail(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode")
    @Operation(summary = "修改邮箱-发送验证码")
    public ApiResultVO<String> updateEmailSendCode() {
        return ApiResultVO.okMsg(baseService.updateEmailSendCode());
    }

    @PostMapping(value = "/updateEmail")
    @Operation(summary = "修改邮箱")
    public ApiResultVO<String> updateEmail(@RequestBody @Valid SignPhoneUpdateEmailDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmail(dto));
    }

    @PostMapping(value = "/setWx/getQrCodeUrl")
    @Operation(summary = "设置微信：获取二维码地址")
    public ApiResultVO<GetQrCodeVO> setWxGetQrCodeUrl(@RequestBody @Valid SignPhoneSetWxGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setWxGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/setWx")
    @Operation(summary = "设置微信")
    public ApiResultVO<SysQrCodeSceneBindVO> setWx(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setWx(notNullId));
    }

    @PostMapping(value = "/updateWx/sendCode")
    @Operation(summary = "修改微信：发送验证码")
    public ApiResultVO<GetQrCodeVO> updateWxSendCode(@RequestBody @Valid SignPhoneUpdateWxSendCodeDTO dto) {
        return ApiResultVO.okData(baseService.updateWxSendCode(dto));
    }

    @PostMapping(value = "/updateWx/getQrCodeUrl/new")
    @Operation(summary = "修改微信：获取新的二维码地址")
    public ApiResultVO<GetQrCodeVO> updateWxGetQrCodeUrlNew(@RequestBody @Valid SignPhoneUpdateWxGetQrCodeUrlNewDTO dto) {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlNew(dto));
    }

    @PostMapping(value = "/updateWx")
    @Operation(summary = "修改微信")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWx(@RequestBody @Valid SignPhoneUpdateWxDTO dto) {
        return ApiResultVO.okData(baseService.updateWx(dto));
    }

    @PostMapping(value = "/updatePhone/sendCode")
    @Operation(summary = "修改手机-发送验证码")
    public ApiResultVO<String> updatePhoneSendCode() {
        return ApiResultVO.okMsg(baseService.updatePhoneSendCode());
    }

    @PostMapping(value = "/updatePhone")
    @Operation(summary = "修改手机")
    public ApiResultVO<String> updatePhone(@RequestBody @Valid SignPhoneUpdatePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePhone(dto));
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
