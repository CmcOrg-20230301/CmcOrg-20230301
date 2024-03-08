package com.cmcorg20230301.be.engine.im.session.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionRefUserJoinUserIdSetDTO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionRefUserQueryRefUserInfoMapVO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndLongSet;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/sys/im/session/refUser")
@Tag(name = "基础-即时通讯-会话-用户-管理")
public class SysImSessionRefUserController {

    @Resource
    SysImSessionRefUserService baseService;

    @Operation(summary = "加入新用户")
    @PostMapping("/join/userIdSet")
    @PreAuthorize("hasAuthority('sysImSessionRefUser:joinUserIdSet')")
    public ApiResultVO<String> joinUserIdSet(@RequestBody @Valid SysImSessionRefUserJoinUserIdSetDTO dto) {
        return ApiResultVO.okMsg(baseService.joinUserIdSet(dto));
    }

    @Operation(summary = "查询：当前会话的用户信息，map")
    @PostMapping("/query/refUserInfoMap")
    public ApiResultVO<LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO>>
        queryRefUserInfoMap(@RequestBody @Valid NotNullIdAndLongSet notNullIdAndLongSet) {
        return ApiResultVO.okData(baseService.queryRefUserInfoMap(notNullIdAndLongSet));
    }

}
