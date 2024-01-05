package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/sys/im/session/refUser")
@Tag(name = "基础-即时通讯-会话-用户-管理")
public class SysImSessionRefUserController {

    @Resource
    SysImSessionRefUserService baseService;

    @Operation(summary = "加入新用户")
    @PostMapping("/join/userIdSet")
    public ApiResultVO<String> joinUserIdSet(@RequestBody @Valid NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet) {
        return ApiResultVO.okData(baseService.joinUserIdSet(notNullIdAndNotEmptyLongSet));
    }

}
