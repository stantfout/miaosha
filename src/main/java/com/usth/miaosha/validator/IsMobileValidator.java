package com.usth.miaosha.validator;

import com.alibaba.druid.util.StringUtils;
import com.usth.miaosha.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean requried = false;

    @Override
    public void initialize(IsMobile isMobile) {
        requried = isMobile.requried();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(requried) {
            return ValidatorUtil.isMobile(value);
        } else {
            if(StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
