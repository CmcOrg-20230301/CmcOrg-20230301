package com.cmcorg20230301.be.engine.sign.signinname.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.SysQrCodeSceneBindExistUserDTO;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;
import com.cmcorg20230301.be.engine.sign.signinname.service.SignSignInNameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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
    public ApiResultVO<GetQrCodeVO> setWxGetQrCodeUrl(@RequestBody @Valid SignSignInNameSetWxGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setWxGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/setWx")
    @Operation(summary = "设置微信")
    public ApiResultVO<SysQrCodeSceneBindVO> setWx(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setWx(notNullId));
    }

    @PostMapping(value = "/setWx/existUser")
    @Operation(summary = "设置微信-存在用户")
    public ApiResultVO<SysQrCodeSceneBindVO> setWxExistUser(@RequestBody @Valid SysQrCodeSceneBindExistUserDTO dto) {
        return ApiResultVO.okMsg(baseService.setWxExistUser(dto));
    }

    @PostMapping(value = "/setEmail/sendCode")
    @Operation(summary = "设置手机：发送验证码")
    public ApiResultVO<String> setPhoneSendCode(@RequestBody @Valid SignSignInNameSetPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhoneSendCode(dto));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<String> setPhone(@RequestBody @Valid SignSignInNameSetPhoneDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhone(dto));
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<String> signDelete(@RequestBody @Valid SignSignInNameSignDeleteDTO dto) {
        return ApiResultVO.okMsg(baseService.signDelete(dto));
    }

}
