package com.cmcorg20230301.be.engine.tenant.controller;

import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/sys/tenantRefUser")
@Tag(name = "租户-用户管理")
public class SysTenantRefUserController {

    @Resource
    SysTenantRefUserService baseService;

}
