package validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("xRangeValidator")
public class XRangeValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        if (!(value instanceof Number)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "X должен быть числом",
                    null
            ));
        }
        double v = ((Number) value).doubleValue();
        // Закрытый интервал [-3; 3]
        if (v < -3 || v > 3) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "X должен быть в диапазоне [-3; 3]",
                    null
            ));
        }
    }
}