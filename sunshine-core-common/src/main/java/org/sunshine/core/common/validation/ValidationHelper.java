package org.sunshine.core.common.validation;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.sunshine.core.tool.exception.BusinessException;

/**
 * @author Teamo
 * @since 2025/5/25
 */
public class ValidationHelper {

    /**
     * 对象校验
     *
     * @param validator 校验器
     * @param target    校验对象
     */
    public static void check(Validator validator, Object target) {
        if (validator == null || target == null) {
            BusinessException.throwException("参数校验失败");
        }
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
        validator.validate(target, errors);
        if (errors.hasErrors()) {
            BusinessException.throwException(errors.getAllErrors().get(0).getDefaultMessage());
        }
    }
}
