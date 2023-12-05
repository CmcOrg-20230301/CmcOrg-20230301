package com.cmcorg20230301.be.engine.sign.signinname.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
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
@Tag(name = "登录注册-登录名")
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
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.signInPassword(dto));
    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "修改密码")
    public ApiResultVO<String> updatePassword(@RequestBody @Valid SignSignInNameUpdatePasswordDTO dto) {
        return ApiResultVO.okMsg(baseService.updatePassword(dto));
    }

    @PostMapping(value = "/updateAccount")
    @Operation(summary = "修改账号")
    public ApiResultVO<String> updateAccount(@RequestBody @Valid SignSignInNameUpdateAccountDTO dto) {
        return ApiResultVO.okMsg(baseService.updateAccount(dto));
    }

    @PostMapping(value = "/signDelete")
    @Operation(summary = "账号注销")
    public ApiResultVO<String> signDelete(@RequestBody @Valid SignSignInNameSignDeleteDTO dto) {
        return ApiResultVO.okMsg(baseService.signDelete(dto));
    }

}
