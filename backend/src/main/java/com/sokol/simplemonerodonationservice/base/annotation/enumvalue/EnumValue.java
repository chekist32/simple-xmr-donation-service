package com.sokol.simplemonerodonationservice.base.annotation.enumvalue;


import jakarta.validation.Constraint;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid value";

}
