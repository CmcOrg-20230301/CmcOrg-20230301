package com.cmcorg20230301.be.engine.other.app.controller;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxVerifyDTO;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppOfficialAccountWxService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 备注：/sys/otherApp/officialAccount/wx/** 下的所有路径都不需要登录即可访问
 */
@RequestMapping("/sys/otherApp/officialAccount/wx")
@RestController
@Tag(name = "第三方应用-公众号-微信-管理")
public class SysOtherAppOfficialAccountWxController {

    @Resource
    SysOtherAppOfficialAccountWxService baseService;

    /**
     * 微信公众号 token验证
     */
    @GetMapping
    public String verify(SysOtherAppOfficialAccountWxVerifyDTO dto) {
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
