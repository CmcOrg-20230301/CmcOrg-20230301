package com.cmcorg20230301.be.engine.sign.wx.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;
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
@Tag(name = "登录注册-微信")
public class SignWxController {

    @Resource
    SignWxService signWxService;

    @PostMapping(value = "/sign/in/miniProgram/phoneCode")
    @Operation(summary = "小程序：手机号 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInMiniProgramPhoneCode(@RequestBody @Valid SignInMiniProgramPhoneCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInMiniProgramPhoneCode(dto));
    }

    @PostMapping(value = "/sign/in/miniProgram/code")
    @Operation(summary = "小程序：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInMiniProgramCode(@RequestBody @Valid SignInMiniProgramCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInMiniProgramCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code")
    @Operation(summary = "浏览器：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInBrowserCode(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInBrowserCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code/userInfo")
    @Operation(summary = "浏览器：微信 code登录，可以获取用户的基础信息", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInBrowserCodeUserInfo(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInBrowserCodeUserInfo(dto));
    }

}
