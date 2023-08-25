package com.cmcorg20230301.be.engine.model.validator;

import com.cmcorg20230301.be.engine.model.model.annotation.NotBlankPattern;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class NotBlankPatternValidator implements ConstraintValidator<NotBlankPattern, CharSequence> {

    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private java.util.regex.Pattern pattern;
    private String escapedRegexp;

    @Override
    public void initialize(NotBlankPattern parameters) {

        Pattern.Flag[] flags = parameters.flags();

        int intFlag = 0;

        for (Pattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        try {

            pattern = java.util.regex.Pattern.compile(parameters.regexp(), intFlag);

        } catch (PatternSyntaxException e) {

            throw LOG.getInvalidRegularExpressionException(e);

        }

        escapedRegexp = InterpolationHelper.escapeMessageParameter(parameters.regexp());

    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null || value.length() == 0) {
            return true;
        }

        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {

            constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("regexp", escapedRegexp);

        }

        Matcher m = pattern.matcher(value);

        return m.matches();

    }

}
