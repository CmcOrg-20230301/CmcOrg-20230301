package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * 我的验证工具类
 */
public class MyValidUtil {

    /**
     * 执行：验证
     */
    public static <T> Set<ConstraintViolation<T>> valid(T t) {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> errSet = validator.validate(t);

        return errSet;

    }

    /**
     * 执行：验证
     *
     * @return null 则表示验证成功
     */
    public static <T> String validReturnStr(T t) {

        Set<ConstraintViolation<T>> errSet = valid(t);

        if (CollUtil.isEmpty(errSet)) {
            return null;
        }

        return errSet.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()).toString();

    }

    /**
     * 执行：验证
     */
    public static <T> void validWillError(T t) {

        String errMsg = validReturnStr(t);

        if (StrUtil.isBlank(errMsg)) {
            return;
        }

        ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, errMsg);

    }

}
