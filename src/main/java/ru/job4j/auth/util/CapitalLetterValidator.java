package ru.job4j.auth.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CapitalLetterValidator implements ConstraintValidator<CapitalLetter, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            return value.chars().filter(Character::isUpperCase).findFirst().isEmpty();
        }
        return true;
    }
}
