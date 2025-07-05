package com.nksoft.entrance_examination.common.validator.annotations;

import com.nksoft.entrance_examination.common.validator.classes.DecimalPrecisionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DecimalPrecisionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalPrecision {
    String message() default "Number must have at most {maxDecimalPlaces} decimal places";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int maxDecimalPlaces() default 1;
}
