package com.ocs.busi.helper;

import com.ocs.common.exception.ServiceException;
import org.apache.poi.ss.formula.functions.T;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidateHelper {

    /**
     * 校验器
     *
     * @param t   参数
     * @param <T> 参数类型
     * @return
     */
    public static <T> List<String> valid(T t) {
        Validator validatorFactory = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validatorFactory.validate(t);
        return errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());
    }

    public static <T> void validData(T t) {
        List<String> message = valid(t);
        if (message.size() > 0) {
            throw new ServiceException(message.toString());
        }
    }
}
