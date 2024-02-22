package com.sokol.simplemonerodonationservice.base.annotation.enumvalue;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumValidator implements ConstraintValidator<EnumValue, String> {
    private List<String> enumValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name).toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = enumValues.contains(value);

        if (result) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid value. Must be one of " + enumValues).addConstraintViolation();
        }

        return result;
    }
}
