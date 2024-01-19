package com.cmcorg20230301.be.engine.sign.email.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;
import com.cmcorg20230301.be.engine.sign.email.service.SignEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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
    public ApiResultVO<String> setSignInNameSendCode() {
        return ApiResultVO.okMsg(baseService.setSignInNameSendCode());
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<String> setSignInName(@RequestBody @Valid SignEmailSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/sendCode")
    @Operation(summary = "修改登录名-发送验证码")
    public ApiResultVO<String> updateSignInNameSendCode() {
        return ApiResultVO.okMsg(baseService.updateSignInNameSendCode());
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<String> updateSignInName(@RequestBody @Valid SignEmailSetSignInNameDTO dto) {
        return ApiResultVO.okMsg(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode/new")
    @Operation(summary = "修改邮箱-发送新邮箱验证码")
    public ApiResultVO<String> updateEmailSendCodeNew(@RequestBody @Valid EmailNotBlankDTO dto) {
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
    public ApiResultVO<GetQrCodeVO> setWxGetQrCodeUrl(@RequestBody @Valid SignEmailSetWxGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setWxGetQrCodeUrl(dto));
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
    public ApiResultVO<String> setPhoneSendCodePhone(SignEmailSetPhoneSendCodePhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhoneSendCodePhone(dto));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<String> setPhone(@RequestBody @Valid SignEmailSetPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhone(dto));
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
