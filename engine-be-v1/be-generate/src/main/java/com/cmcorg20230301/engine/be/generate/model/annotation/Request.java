package com.cmcorg20230301.engine.be.generate.model.annotation;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

    @Schema(description = "请求路径") String[] uriArr() default {};

    @Schema(description = "请求方式") HttpMethod method() default HttpMethod.POST;

    @Schema(description = "是否忽略") boolean ignoreFlag() default false;

}
