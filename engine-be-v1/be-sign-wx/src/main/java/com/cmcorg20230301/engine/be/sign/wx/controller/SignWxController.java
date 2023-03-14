package com.cmcorg20230301.engine.be.sign.wx.controller;

import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInPhoneCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.service.SignWxService;
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

    @PostMapping(value = "/sign/in/phoneCode")
    @Operation(summary = "手机号 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInPhoneCode(@RequestBody @Valid SignInPhoneCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInPhoneCode(dto));
    }

    @PostMapping(value = "/sign/in/code")
    @Operation(summary = "微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<String> signInCode(@RequestBody @Valid SignInCodeDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, signWxService.signInCode(dto));
    }

}
