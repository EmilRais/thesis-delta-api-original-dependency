package dk.developer.validation.single;

import dk.developer.validation.GenericValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailConstraintValidator implements ConstraintValidator<Email, String> {
    @Override
    public void initialize(Email constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return GenericValidator.get().isEmail(value).result();
    }
}
