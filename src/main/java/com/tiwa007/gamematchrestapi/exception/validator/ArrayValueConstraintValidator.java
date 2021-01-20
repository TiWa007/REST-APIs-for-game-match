package com.tiwa007.gamematchrestapi.exception.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

//https://www.cnblogs.com/phdeblog/p/13795252.html
public class ArrayValueConstraintValidator implements ConstraintValidator<InStringArray, String> {

    private Set<String> set = new HashSet<>();

    @Override
    public void initialize(InStringArray constraintAnnotation) {
        String[] values = constraintAnnotation.values();
        for (String value : values) {
            set.add(value);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        return set.contains(value);
    }
}
