package com.cmcorg20230301.be.engine.sign.wx.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.*;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sign/wx")
@Tag(name = "基础-登录注册-微信")
public class SignWxController {

    @Resource
    SignWxService baseService;

    @PostMapping(value = "/sign/in/miniProgram/phoneCode")
    @Operation(summary = "小程序：手机号 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInMiniProgramPhoneCode(@RequestBody @Valid SignInMiniProgramPhoneCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInMiniProgramPhoneCode(dto));
    }

    @PostMapping(value = "/sign/in/miniProgram/code")
    @Operation(summary = "小程序：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInMiniProgramCode(@RequestBody @Valid SignInMiniProgramCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInMiniProgramCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code")
    @Operation(summary = "浏览器：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInBrowserCode(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInBrowserCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code/userInfo")
    @Operation(summary = "浏览器：微信 code登录，可以获取用户的基础信息", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInBrowserCodeUserInfo(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInBrowserCodeUserInfo(dto));
    }

    @PostMapping(value = "/sign/in/getQrCodeUrl")
    @Operation(summary = "扫码登录：获取二维码")
    public ApiResultVO<GetQrCodeVO> signInGetQrCodeUrl(@RequestBody @Valid UserSignBaseDTO dto) {
        return ApiResultVO.okData(baseService.signInGetQrCodeUrl(dto, true));
    }

    @PostMapping(value = "/sign/in/byQrCodeId")
    @Operation(summary = "扫码登录：通过二维码 id")
    public ApiResultVO<SignInVO> signInByQrCodeId(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signInByQrCodeId(notNullId));
    }

    @PostMapping(value = "/setPassword/getQrCodeUrl")
    @Operation(summary = "设置密码-获取二维码")
    public ApiResultVO<GetQrCodeVO> setPasswordGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setPasswordGetQrCodeUrl());
    }

    @PostMapping(value = "/setPassword")
    @Operation(summary = "设置密码")
    public ApiResultVO<SysQrCodeSceneBindVO> setPassword(@RequestBody @Valid SignWxSetPasswordDTO dto) {
        return ApiResultVO.okData(baseService.setPassword(dto));
    }

    @PostMapping(value = "/updatePassword/getQrCodeUrl")
    @Operation(summary = "修改密码-获取二维码")
    public ApiResultVO<GetQrCodeVO> updatePasswordGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.updatePasswordGetQrCodeUrl());
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<SysQrCodeSceneBindVO> updatePassword(@RequestBody @Valid SignWxUpdatePasswordDTO dto) {
        return ApiResultVO.okData(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/setSignInName/getQrCodeUrl")
    @Operation(summary = "设置登录名-获取二维码")
    public ApiResultVO<GetQrCodeVO> setSignInNameGetQrCodeUrl(@RequestBody @Valid SignWxSetSignInNameGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setSignInNameGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<SysQrCodeSceneBindVO> setSignInName(@RequestBody @Valid SignWxSetSignInNameDTO dto) {
        return ApiResultVO.okData(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/getQrCodeUrl")
    @Operation(summary = "修改登录名-获取二维码")
    public ApiResultVO<GetQrCodeVO> updateSignInNameGetQrCodeUrl(@RequestBody @Valid SignWxUpdateSignInNameGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.updateSignInNameGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/updateSignInName")
    @Operation(summary = "修改登录名")
    public ApiResultVO<SysQrCodeSceneBindVO> updateSignInName(@RequestBody @Valid SignWxUpdateSignInNameDTO dto) {
        return ApiResultVO.okData(baseService.updateSignInName(dto));
    }

    @PostMapping(value = "/setEmail/sendCode")
    @Operation(summary = "设置邮箱：发送验证码")
    public ApiResultVO<String> setEmailSendCode(@RequestBody @Valid SignWxSetEmailSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setEmailSendCode(dto));
    }

    @PostMapping(value = "/setEmail/getQrCodeUrl")
    @Operation(summary = "设置邮箱-获取二维码")
    public ApiResultVO<GetQrCodeVO> setEmailGetQrCodeUrl(@RequestBody @Valid SignWxSetEmailGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setEmailGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/setEmail")
    @Operation(summary = "设置邮箱")
    public ApiResultVO<SysQrCodeSceneBindVO> setEmail(@RequestBody @Valid SignWxSetEmailDTO dto) {
        return ApiResultVO.okData(baseService.setEmail(dto));
    }

    @PostMapping(value = "/updateEmail/sendCode")
    @Operation(summary = "修改邮箱：发送验证码")
    public ApiResultVO<String> updateEmailSendCode(@RequestBody @Valid SignWxUpdateEmailSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.updateEmailSendCode(dto));
    }

    @PostMapping(value = "/updateEmail/getQrCodeUrl")
    @Operation(summary = "修改邮箱-获取二维码")
    public ApiResultVO<GetQrCodeVO> updateEmailGetQrCodeUrl(@RequestBody @Valid SignWxUpdateEmailGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.updateEmailGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/updateEmail")
    @Operation(summary = "修改邮箱")
    public ApiResultVO<SysQrCodeSceneBindVO> updateEmail(@RequestBody @Valid SignWxUpdateEmailDTO dto) {
        return ApiResultVO.okData(baseService.updateEmail(dto));
    }

    @PostMapping(value = "/updateWx/getQrCodeUrl/old")
    @Operation(summary = "修改微信：获取旧的二维码地址")
    public ApiResultVO<GetQrCodeVO> updateWxGetQrCodeUrlOld() {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlOld());
    }

    @PostMapping(value = "/updateWx/getQrCodeUrl/new")
    @Operation(summary = "修改微信：获取新的二维码地址")
    public ApiResultVO<GetQrCodeVO> updateWxGetQrCodeUrlNew() {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlNew());
    }

    @PostMapping(value = "/updateWx")
    @Operation(summary = "修改微信")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWx(@RequestBody @Valid SignWxUpdateWxDTO dto) {
        return ApiResultVO.okData(baseService.updateWx(dto));
    }

    @PostMapping(value = "/setPhone/sendCode")
    @Operation(summary = "设置手机：发送验证码")
    public ApiResultVO<String> setPhoneSendCode(@RequestBody @Valid SignWxSetPhoneSendCodeDTO dto) {
        return ApiResultVO.okMsg(baseService.setPhoneSendCode(dto));
    }

    @PostMapping(value = "/setPhone/getQrCodeUrl")
    @Operation(summary = "设置手机：获取二维码")
    public ApiResultVO<GetQrCodeVO> setPhoneGetQrCodeUrl(@RequestBody @Valid SignWxSetPhoneGetQrCodeUrlDTO dto) {
        return ApiResultVO.okData(baseService.setPhoneGetQrCodeUrl(dto));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<SysQrCodeSceneBindVO> setPhone(@RequestBody @Valid SignWxSetPhoneDTO dto) {
        return ApiResultVO.okData(baseService.setPhone(dto));
    }

    @PostMapping(value = "/signDelete/getQrCodeUrl")
    @Operation(summary = "账号注销-获取二维码")
    public ApiResultVO<GetQrCodeVO> signDeleteGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.signDeleteGetQrCodeUrl());
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<SysQrCodeSceneBindVO> signDelete(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signDelete(notNullId));
    }

}
