package com.cmcorg20230301.engine.be.generate.model.annotation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestClass {

    @Schema(description = "忽略的字段，多个用逗号隔开，优先级高于字段上面的注解，目的：隐藏继承父类的字段") String tableIgnoreFields() default "";

}
