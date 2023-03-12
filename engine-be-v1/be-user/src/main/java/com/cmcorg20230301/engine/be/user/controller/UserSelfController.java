package com.cmcorg20230301.engine.be.user.controller;

import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.engine.be.user.model.vo.UserSelfInfoVO;
import com.cmcorg20230301.engine.be.user.service.UserSelfService;
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
@Tag(name = "用户-自我-管理")
public class UserSelfController {

    @Resource
    UserSelfService baseService;

    @Operation(summary = "获取：当前用户，基本信息")
    @PostMapping(value = "/info")
    public ApiResultVO<UserSelfInfoVO> userSelfInfo() {
        return ApiResultVO.ok(baseService.userSelfInfo());
    }

    @Operation(summary = "当前用户：基本信息：修改")
    @PostMapping(value = "/updateInfo")
    public ApiResultVO<String> userSelfUpdateInfo(@RequestBody @Valid UserSelfUpdateInfoDTO dto) {
        return ApiResultVO.ok(baseService.userSelfUpdateInfo(dto));
    }

}
