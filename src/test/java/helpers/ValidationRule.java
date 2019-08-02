package helpers;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class ValidationRule {
    private ValidationTesting validationTesting;

    @Getter(AccessLevel.PACKAGE)
    private String paramName;

    @Getter(AccessLevel.PACKAGE)
    private List<Class> validatingAnnotations;

    @Getter(AccessLevel.PACKAGE)
    private String errorMessage;

    @Getter(AccessLevel.PACKAGE)
    private String testingUrl;

    @Getter(AccessLevel.PACKAGE)
    private String[] pathVars;

    ValidationRule(ValidationTesting validationTesting, String paramName) {
        this.validationTesting = validationTesting;
        this.paramName = paramName;
    }

    ValidationRule shouldBeValidatedBy(Class... annotationClass) {
        this.validatingAnnotations = Arrays.asList(annotationClass);
        return this;
    }

    ValidationTesting testRequest(String url, String errorMessage, String... pathVar) {
        this.errorMessage = errorMessage;
        this.pathVars = pathVar;
        this.testingUrl = url;
        return validationTesting.setValidationRules(this);
    }
}
