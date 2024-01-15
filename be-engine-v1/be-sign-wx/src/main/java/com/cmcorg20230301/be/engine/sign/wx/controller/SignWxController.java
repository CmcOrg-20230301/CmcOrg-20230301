package com.cmcorg20230301.be.engine.sign.wx.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
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
@Tag(name = "基础-登录注册-微信")
public class SignWxController {

    @Resource
    SignWxService signWxService;

    @PostMapping(value = "/sign/in/miniProgram/phoneCode")
    @Operation(summary = "小程序：手机号 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInMiniProgramPhoneCode(@RequestBody @Valid SignInMiniProgramPhoneCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInMiniProgramPhoneCode(dto));
    }

    @PostMapping(value = "/sign/in/miniProgram/code")
    @Operation(summary = "小程序：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInMiniProgramCode(@RequestBody @Valid SignInMiniProgramCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInMiniProgramCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code")
    @Operation(summary = "浏览器：微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInBrowserCode(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInBrowserCode(dto));
    }

    @PostMapping(value = "/sign/in/browser/code/userInfo")
    @Operation(summary = "浏览器：微信 code登录，可以获取用户的基础信息", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInBrowserCodeUserInfo(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInBrowserCodeUserInfo(dto));
    }

    @PostMapping(value = "/sign/in/getQrCodeUrl")
    @Operation(summary = "扫码登录：获取二维码")
    public ApiResultVO<GetQrCodeVO> getQrCodeUrl(@RequestBody @Valid UserSignBaseDTO dto) {
        return ApiResultVO.okData(signWxService.getQrCodeUrl(dto));
    }

    @PostMapping(value = "/sign/in/queryQrCodeById")
    @Operation(summary = "扫码登录：查询二维码数据")
    public ApiResultVO<SignInVO> queryQrCodeById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okData(signWxService.queryQrCodeById(notNullId));
    }

}
