package com.cmcorg20230301.engine.be.sign.email.controller;

import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.sign.email.dto.*;
import com.cmcorg20230301.engine.be.sign.email.service.SignEmailService;
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
@Tag(name = "登录注册-邮箱")
public class SignEmailController {

    @Resource
    SignEmailService baseService;

    @PostMapping(value = "/sign/up/sendCode")
    @Operation(summary = "注册-发送验证码")
    public ApiResultVO<String> signUpSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.ok(baseService.signUpSendCode(dto));
    }

    @PostMapping(value = "/sign/up")
    @Operation(summary = "注册")
    public ApiResultVO<String> signUp(@RequestBody @Valid SignEmailSignUpDTO dto) {
        return ApiResultVO.ok(baseService.signUp(dto));
    }

    @PostMapping(value = "/sign/in/password")
    @Operation(summary = "邮箱账号密码登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInPassword(@RequestBody @Valid SignEmailSignInPasswordDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.signInPassword(dto));
    }

    @PostMapping(value = "/updatePassword/sendCode")
    @Operation(summary = "修改密码-发送验证码")
    public ApiResultVO<String> updatePasswordSendCode() {
        return ApiResultVO.ok(baseService.updatePasswordSendCode());
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SignEmailUpdatePasswordDTO dto) {
        return ApiResultVO.ok(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/updateAccount/sendCode")
    @Operation(summary = "修改邮箱-发送验证码")
    public ApiResultVO<String> updateAccountSendCode() {
        return ApiResultVO.ok(baseService.updateAccountSendCode());
    }

    @PostMapping(value = "/updateAccount")
    @Operation(summary = "修改邮箱")
    public ApiResultVO<String> updateAccount(@RequestBody @Valid SignEmailUpdateAccountDTO dto) {
        return ApiResultVO.ok(baseService.updateAccount(dto));
    }

    @PostMapping(value = "/forgetPassword/sendCode")
    @Operation(summary = "忘记密码-发送验证码")
    public ApiResultVO<String> forgetPasswordSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.ok(baseService.forgetPasswordSendCode(dto));
    }

    @PostMapping(value = "/forgetPassword")
    @Operation(summary = "忘记密码")
    public ApiResultVO<String> forgetPassword(@RequestBody @Valid SignEmailForgetPasswordDTO dto) {
        return ApiResultVO.ok(baseService.forgetPassword(dto));
    }

    @PostMapping(value = "/signDelete/sendCode")
    @Operation(summary = "账号注销-发送验证码")
    public ApiResultVO<String> signDeleteSendCode() {
        return ApiResultVO.ok(baseService.signDeleteSendCode());
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<String> signDelete(@RequestBody @Valid NotBlankCodeDTO dto) {
        return ApiResultVO.ok(baseService.signDelete(dto));
    }

    @PostMapping(value = "/bindAccount/sendCode")
    @Operation(summary = "绑定邮箱-发送验证码")
    public ApiResultVO<String> bindAccountSendCode(@RequestBody @Valid EmailNotBlankDTO dto) {
        return ApiResultVO.ok(baseService.bindAccountSendCode(dto));
    }

    @PostMapping(value = "/bindAccount")
    @Operation(summary = "绑定邮箱")
    public ApiResultVO<String> bindAccount(@RequestBody @Valid SignEmailBindAccountDTO dto) {
        return ApiResultVO.ok(baseService.bindAccount(dto));
    }

}
