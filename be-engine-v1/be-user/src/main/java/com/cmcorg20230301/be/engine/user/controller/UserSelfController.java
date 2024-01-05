package com.cmcorg20230301.be.engine.user.controller;

import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.be.engine.user.model.vo.UserSelfInfoVO;
import com.cmcorg20230301.be.engine.user.service.UserSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/user/self")
@Tag(name = "基础-用户-自我-管理")
public class UserSelfController {

    @Resource
    UserSelfService baseService;

    @Operation(summary = "获取：当前用户，基本信息")
    @PostMapping(value = "/info")
    public ApiResultVO<UserSelfInfoVO> userSelfInfo() {
        return ApiResultVO.okData(baseService.userSelfInfo());
    }

    @Operation(summary = "当前用户：基本信息：修改")
    @PostMapping(value = "/updateInfo")
    public ApiResultVO<String> userSelfUpdateInfo(@RequestBody @Valid UserSelfUpdateInfoDTO dto) {
        return ApiResultVO.okMsg(baseService.userSelfUpdateInfo(dto));
    }

    @Operation(summary = "当前用户：刷新jwt私钥后缀")
    @PostMapping(value = "/refreshJwtSecretSuf")
    public ApiResultVO<String> userSelfRefreshJwtSecretSuf() {
        return ApiResultVO.okMsg(baseService.userSelfRefreshJwtSecretSuf());
    }

    @Operation(summary = "当前用户：重置头像")
    @PostMapping(value = "/resetAvatar")
    public ApiResultVO<String> userSelfResetAvatar() {
        return ApiResultVO.okMsg(baseService.userSelfResetAvatar());
    }

}
