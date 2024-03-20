package com.cmcorg20230301.be.engine.jwt.refresh.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcorg20230301.be.engine.jwt.refresh.model.dto.SysJwtRefreshSignInRefreshTokenDTO;
import com.cmcorg20230301.be.engine.jwt.refresh.service.SysJwtRefreshService;
import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "基础-jwt-刷新-管理")
@RestController
@RequestMapping("/sys/jwt/refresh")
public class SysJwtRefreshController {

    @Resource
    SysJwtRefreshService baseService;

    @PostMapping(value = "/sign/in/refreshToken")
    @Operation(summary = "通过：refreshToken登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInRefreshToken(@RequestBody @Valid SysJwtRefreshSignInRefreshTokenDTO dto) {
        return ApiResultVO.okData(baseService.signInRefreshToken(dto));
    }

}
