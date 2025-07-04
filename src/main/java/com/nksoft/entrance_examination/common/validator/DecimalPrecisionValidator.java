package com.nksoft.entrance_examination.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DecimalPrecisionValidator implements ConstraintValidator<DecimalPrecision, Number> {
    private int maxDecimalPlaces;
    private static final double EPSILON = 1e-3;

    @Override
    public void initialize(DecimalPrecision constraintAnnotation) {
        this.maxDecimalPlaces = constraintAnnotation.maxDecimalPlaces();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) return true;

        double scaled = value.doubleValue() * Math.pow(10, maxDecimalPlaces);
        return Math.abs(Math.round(scaled) - scaled) < EPSILON;
    }
}
