package com.cmcorg20230301.engine.be.generate.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "页面类型")
public enum PageTypeEnum {

    SIGN, // 登录注册页面
    ADMIN, // 后台管理页面
    NONE, // 无

    ;

}
