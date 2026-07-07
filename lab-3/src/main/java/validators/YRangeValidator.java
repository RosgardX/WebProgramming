package validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("yRangeValidator")
public class YRangeValidator implements Validator<Object> {

    private static final String NUM_REGEX = "^-?\\d+(\\.\\d+)?$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) return;

        String s = value.toString().trim();
        if (s.isEmpty()) return;

        if (!s.matches(NUM_REGEX)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Y должно быть числом (пример: 1.23)",
                    null
            ));
        }

        double v;
        try {
            v = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Y должно быть числом (пример: 1.23)",
                    null
            ));
        }

        // Открытый интервал (-5; 5)
        if (v <= -5 || v >= 5) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Y должно быть в диапазоне (-5; 5)",
                    null
            ));
        }
    }
}