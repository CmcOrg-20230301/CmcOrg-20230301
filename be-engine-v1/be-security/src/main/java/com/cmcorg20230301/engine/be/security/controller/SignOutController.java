package com.cmcorg20230301.engine.be.security.controller;

import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.service.SignOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/sign/out")
@Tag(name = "退出登录")
public class SignOutController {

    @Resource
    SignOutService baseService;

    @PostMapping(value = "/self")
    @Operation(summary = "当前用户-退出登录")
    public ApiResultVO<String> signOut() {
        return ApiResultVO.okMsg(baseService.signOut());
    }

}
