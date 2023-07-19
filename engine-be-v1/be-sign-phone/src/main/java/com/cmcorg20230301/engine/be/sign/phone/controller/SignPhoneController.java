package com.cmcorg20230301.engine.be.sign.phone.controller;

import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.*;
import com.cmcorg20230301.engine.be.sign.phone.service.SignPhoneService;
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
@Tag(name = "登录注册-手机")
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
    @Operation(summary = "手机账号密码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInPassword(@RequestBody @Valid SignPhoneSignInPasswordDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.signInPassword(dto));
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

    @PostMapping(value = "/updateAccount/sendCode")
    @Operation(summary = "修改手机-发送验证码")
    public ApiResultVO<String> updateAccountSendCode() {
        return ApiResultVO.okMsg(baseService.updateAccountSendCode());
    }

    @PostMapping(value = "/updateAccount")
    @Operation(summary = "修改手机")
    public ApiResultVO<String> updateAccount(@RequestBody @Valid SignPhoneUpdateAccountDTO dto) {
        return ApiResultVO.okMsg(baseService.updateAccount(dto));
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

    @PostMapping(value = "/bindAccount/sendCode")
    @Operation(summary = "绑定手机-发送验证码")
    public ApiResultVO<String> bindAccountSendCode(@RequestBody @Valid PhoneNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.bindAccountSendCode(dto));
    }

    @PostMapping(value = "/bindAccount")
    @Operation(summary = "绑定手机")
    public ApiResultVO<String> bindAccount(@RequestBody @Valid SignPhoneBindAccountDTO dto) {
        return ApiResultVO.okMsg(baseService.bindAccount(dto));
    }

    @PostMapping(value = "/sign/in/sendCode")
    @Operation(summary = "手机验证码登录-发送验证码")
    public ApiResultVO<String> signInSendCode(@RequestBody @Valid PhoneNotBlankDTO dto) {
        return ApiResultVO.okMsg(baseService.signInSendCode(dto));
    }

    @PostMapping(value = "/sign/in/code")
    @Operation(summary = "手机验证码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInCode(@RequestBody @Valid SignPhoneSignInCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.signInCode(dto));
    }

}
