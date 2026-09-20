package work.onlinebookshop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch annotation) {
        field = annotation.field();
        fieldMatch = annotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        if (Objects.equals(wrapper.getPropertyValue(field),
                wrapper.getPropertyValue(fieldMatch))) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldMatch)
                .addConstraintViolation();
        return false;
    }
}
