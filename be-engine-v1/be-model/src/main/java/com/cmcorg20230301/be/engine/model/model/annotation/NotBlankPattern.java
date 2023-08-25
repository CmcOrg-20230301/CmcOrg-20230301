package com.cmcorg20230301.be.engine.model.model.annotation;

import com.cmcorg20230301.be.engine.model.validator.NotBlankPatternValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不检查：空字符串的正则表达式
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(NotBlankPattern.List.class)
@Documented
@Constraint(validatedBy = {NotBlankPatternValidator.class})
public @interface NotBlankPattern {

    String regexp();

    Pattern.Flag[] flags() default {};

    String message() default "{javax.validation.constraints.Pattern.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NotBlankPattern[] value();

    }

}
