package com.cmcorg20230301.engine.be.generate.model.annotation;

import com.cmcorg20230301.engine.be.generate.model.enums.FormInputTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestField {

    @Schema(description = "表单页面，是否是删除时的提示字段，备注：只会存在一个，会被后面的覆盖") boolean formDeleteNameFlag() default false;

    @Schema(description = "表单页面，字段显示用") String formTitle() default "";

    @Schema(description = "table页面，字段显示用") String tableTitle() default "";

    @Schema(description = "表单页面，字段额外说明") String formTooltip() default "";

    @Schema(description = "table页面，字段排序，越大越前面") int tableOrderNo() default Integer.MIN_VALUE;

    @Schema(description = "表单是否忽略") boolean formIgnoreFlag() default false;

    @Schema(description = "table是否忽略") boolean tableIgnoreFlag() default false;

    @Schema(description = "不在 search里面显示") boolean hideInSearchFlag() default false;

    @Schema(description = "表单输入类型") FormInputTypeEnum formInputType() default FormInputTypeEnum.TEXT;

    @Schema(description = "表单是下拉选时，是否支持多选，备注：此值只针对：下拉选和树形下拉选") boolean formSelectMultipleFlag() default false;

    @Schema(description = "表单是下拉选时，options的值，备注：此值只针对：下拉选和树形下拉选") String formSelectOptionsStr() default "";

    @Schema(description = "表单是下拉选时，request的值，备注：此值只针对：下拉选和树形下拉选") String formSelectRequestStr() default "";

    @Schema(description = "表单是下拉选时，request是否按照 tree格式来获取，备注：此值只针对：下拉选和树形下拉选") boolean formSelectRequestTreeFlag() default false;

    @Schema(description = "表单是下拉选时，options或者request需要导入的包，备注：此值只针对：下拉选和树形下拉选") String formSelectOptionsOrRequestImportStr() default "";

}
