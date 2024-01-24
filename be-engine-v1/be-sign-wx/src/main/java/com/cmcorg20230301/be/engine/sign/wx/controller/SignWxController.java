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
    @Operation(summary = "扫码登录：通过二维码 id", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInByQrCodeId(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signInByQrCodeId(notNullId));
    }

    @PostMapping(value = "/sign/in/single/getQrCodeUrl")
    @Operation(summary = "单点登录：扫码登录：获取二维码")
    public ApiResultVO<GetQrCodeVO> signInSingleGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.signInSingleGetQrCodeUrl(true));
    }

    @PostMapping(value = "/sign/in/single/byQrCodeId")
    @Operation(summary = "单点登录：扫码登录：通过二维码 id", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInSingleByQrCodeId(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signInSingleByQrCodeId(notNullId));
    }

    @PostMapping(value = "/setPassword/getQrCodeUrl")
    @Operation(summary = "设置密码-获取二维码")
    public ApiResultVO<GetQrCodeVO> setPasswordGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setPasswordGetQrCodeUrl());
    }

    @PostMapping(value = "/setPassword/getQrCodeSceneFlag")
    @Operation(summary = "设置密码：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setPasswordGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setPasswordGetQrCodeSceneFlag(notNullId));
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

    @PostMapping(value = "/updatePassword/getQrCodeSceneFlag")
    @Operation(summary = "修改密码：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updatePasswordGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updatePasswordGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<SysQrCodeSceneBindVO> updatePassword(@RequestBody @Valid SignWxUpdatePasswordDTO dto) {
        return ApiResultVO.okData(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/setSignInName/getQrCodeUrl")
    @Operation(summary = "设置登录名-获取二维码")
    public ApiResultVO<GetQrCodeVO> setSignInNameGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setSignInNameGetQrCodeUrl());
    }

    @PostMapping(value = "/setSignInName/getQrCodeSceneFlag")
    @Operation(summary = "设置登录名：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setSignInNameGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setSignInNameGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setSignInName")
    @Operation(summary = "设置登录名")
    public ApiResultVO<SysQrCodeSceneBindVO> setSignInName(@RequestBody @Valid SignWxSetSignInNameDTO dto) {
        return ApiResultVO.okData(baseService.setSignInName(dto));
    }

    @PostMapping(value = "/updateSignInName/getQrCodeUrl")
    @Operation(summary = "修改登录名-获取二维码")
    public ApiResultVO<GetQrCodeVO> updateSignInNameGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.updateSignInNameGetQrCodeUrl());
    }

    @PostMapping(value = "/updateSignInName/getQrCodeSceneFlag")
    @Operation(summary = "修改登录名：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updateSignInNameGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updateSignInNameGetQrCodeSceneFlag(notNullId));
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
    public ApiResultVO<GetQrCodeVO> setEmailGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setEmailGetQrCodeUrl());
    }

    @PostMapping(value = "/setEmail/getQrCodeSceneFlag")
    @Operation(summary = "设置邮箱：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setEmailGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setEmailGetQrCodeSceneFlag(notNullId));
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
    public ApiResultVO<GetQrCodeVO> updateEmailGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.updateEmailGetQrCodeUrl());
    }

    @PostMapping(value = "/updateEmail/getQrCodeSceneFlag")
    @Operation(summary = "修改邮箱：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updateEmailGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updateEmailGetQrCodeSceneFlag(notNullId));
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

    @PostMapping(value = "/updateWx/getQrCodeSceneFlag/old")
    @Operation(summary = "修改微信：获取旧的二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWxGetQrCodeSceneFlagOld(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeSceneFlagOld(notNullId));
    }

    @PostMapping(value = "/updateWx/getQrCodeUrl/new")
    @Operation(summary = "修改微信：获取新的二维码地址")
    public ApiResultVO<GetQrCodeVO> updateWxGetQrCodeUrlNew() {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlNew());
    }

    @PostMapping(value = "/updateWx/getQrCodeSceneFlag/new")
    @Operation(summary = "修改微信：获取新的二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> updateWxGetQrCodeSceneFlagNew(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeSceneFlagNew(notNullId));
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
    public ApiResultVO<GetQrCodeVO> setPhoneGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.setPhoneGetQrCodeUrl());
    }

    @PostMapping(value = "/setPhone/getQrCodeSceneFlag")
    @Operation(summary = "设置手机：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setPhoneGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setPhoneGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/setPhone")
    @Operation(summary = "设置手机")
    public ApiResultVO<SysQrCodeSceneBindVO> setPhone(@RequestBody @Valid SignWxSetPhoneDTO dto) {
        return ApiResultVO.okData(baseService.setPhone(dto));
    }

    @PostMapping(value = "/setSingleSignIn/getQrCodeUrl/current")
    @Operation(summary = "设置统一登录：获取当前微信的二维码地址")
    public ApiResultVO<GetQrCodeVO> setSingleSignInGetQrCodeUrlCurrent() {
        return ApiResultVO.okData(baseService.setSingleSignInGetQrCodeUrlCurrent());
    }

    @PostMapping(value = "/setSingleSignIn/getQrCodeSceneFlag/current")
    @Operation(summary = "设置统一登录：获取当前微信的二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignInGetQrCodeSceneFlagCurrent(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setSingleSignInGetQrCodeSceneFlagCurrent(notNullId));
    }

    @PostMapping(value = "/setSingleSignIn/getQrCodeUrl/singleSignIn")
    @Operation(summary = "设置统一登录：获取统一登录微信的二维码地址")
    public ApiResultVO<GetQrCodeVO> setSingleSignInGetQrCodeUrlSingleSignIn() {
        return ApiResultVO.okData(baseService.updateWxGetQrCodeUrlSingleSignIn());
    }

    @PostMapping(value = "/setSingleSignIn/getQrCodeSceneFlag/singleSignIn")
    @Operation(summary = "设置统一登录：获取统一登录微信的二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignInGetQrCodeSceneFlagSingleSignIn(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.setSingleSignInGetQrCodeSceneFlagSingleSignIn(notNullId));
    }

    @PostMapping(value = "/setSingleSignIn")
    @Operation(summary = "设置统一登录")
    public ApiResultVO<SysQrCodeSceneBindVO> setSingleSignIn(@RequestBody @Valid SignWxSetSingleSignInDTO dto) {
        return ApiResultVO.okData(baseService.setSingleSignIn(dto));
    }

    @PostMapping(value = "/signDelete/getQrCodeUrl")
    @Operation(summary = "账号注销-获取二维码")
    public ApiResultVO<GetQrCodeVO> signDeleteGetQrCodeUrl() {
        return ApiResultVO.okData(baseService.signDeleteGetQrCodeUrl());
    }

    @PostMapping(value = "/signDelete/getQrCodeSceneFlag")
    @Operation(summary = "账号注销：获取二维码是否已经被扫描")
    public ApiResultVO<SysQrCodeSceneBindVO> signDeleteGetQrCodeSceneFlag(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signDeleteGetQrCodeSceneFlag(notNullId));
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<SysQrCodeSceneBindVO> signDelete(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(baseService.signDelete(notNullId));
    }

}
