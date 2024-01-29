package com.cmcorg20230301.be.engine.other.app.wx.controller;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxOfficialAccountVerifyDTO;
import com.cmcorg20230301.be.engine.other.app.wx.service.SysOtherAppWxOfficialAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 备注：/sys/otherApp/wx/officialAccount/** 下的所有路径都不需要登录即可访问
 */
@RequestMapping("/sys/otherApp/wx/officialAccount")
@RestController
@Tag(name = "基础-第三方应用-微信-公众号-管理")
public class SysOtherAppWxOfficialAccountController {

    @Resource
    SysOtherAppWxOfficialAccountService baseService;

    /**
     * 微信公众号 token验证
     */
    @GetMapping
    public String verify(SysOtherAppWxOfficialAccountVerifyDTO dto) {
        return baseService.verify(dto);
    }

    /**
     * 微信公众号：推送的消息
     */
    @PostMapping
    public String receiveMessage(HttpServletRequest request) {
        return baseService.receiveMessage(request);
    }

}
