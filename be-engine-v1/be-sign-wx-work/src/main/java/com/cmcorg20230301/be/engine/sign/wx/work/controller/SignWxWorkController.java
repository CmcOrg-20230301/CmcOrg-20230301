package com.cmcorg20230301.be.engine.sign.wx.work.controller;

import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.work.service.SignWxWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sign/wxWork")
@Tag(name = "基础-登录注册-企业微信")
public class SignWxWorkController {

    @Resource
    SignWxWorkService baseService;

    @PostMapping(value = "/sign/in/browser/code")
    @Operation(summary = "浏览器：企业微信 code登录", description = OperationDescriptionConstant.SIGN_IN)
    public ApiResultVO<SignInVO> signInBrowserCode(@RequestBody @Valid SignInBrowserCodeDTO dto) {
        return ApiResultVO.okData(baseService.signInBrowserCode(dto));
    }

}
