package com.cmcorg20230301.engine.be.generate.model.annotation;

import com.cmcorg20230301.engine.be.generate.model.enums.PageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebPage {

    @Schema(description = "页面路径") String path() default "";

    @Schema(description = "页面类型") PageTypeEnum type() default PageTypeEnum.NONE;

    @Schema(description = "页面标题") String title() default "";

    @Schema(description = "页面图标") String icon() default "";

}
