package com.cmcorg20230301.be.engine.wx.controller;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.wx.service.SysWxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RequestMapping("/sys/wx")
@RestController
@Tag(name = "微信-管理")
public class SysWxController {

    @Resource
    SysWxService baseService;

    @Operation(summary = "微信公众号：同步菜单")
    @PostMapping("/officialAccount/updateMenu")
    @PreAuthorize("hasAuthority('sysWx:officialAccountUpdateMenu')")
    public ApiResultVO<String> officialAccountUpdateMenu(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.okMsg(baseService.officialAccountUpdateMenu(notNullId));
    }

}
