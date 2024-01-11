package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/im/session/refUser")
@Tag(name = "基础-即时通讯-会话-用户-管理")
public class SysImSessionRefUserController {

    @Resource
    SysImSessionRefUserService baseService;

//    @Operation(summary = "加入新用户")
//    @PostMapping("/join/userIdSet")
//    public ApiResultVO<String> joinUserIdSet(@RequestBody @Valid NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet) {
//        return ApiResultVO.okMsg(baseService.joinUserIdSet(notNullIdAndNotEmptyLongSet));
//    }

//    @Operation(summary = "查询：当前会话的用户信息，map")
//    @PostMapping("/query/refUserInfoMap")
//    public ApiResultVO<LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO>> queryRefUserInfoMap(@RequestBody @Valid NotNullIdAndLongSet notNullIdAndLongSet) {
//        return ApiResultVO.okData(baseService.queryRefUserInfoMap(notNullIdAndLongSet));
//    }

}
