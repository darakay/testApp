package helpers;


import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ValidationTesting {

    @Getter(AccessLevel.PACKAGE)
    private Class resource;

    @Getter(AccessLevel.PACKAGE)
    private Map<String, ValidationRule> validationRules = new HashMap<>();


    public ValidationTesting url(String url) {
        return null;
    }

    public ValidationTesting pathVarShouldBeValidatedBy(Class annotationClass) {
        return null;
    }


    public ValidationTesting resource(Class controllerClass) {
        this.resource = controllerClass;
        return this;
    }

    public ValidationRule allPathVarsWithName(String name) {
        return new ValidationRule(this, name);
    }

    ValidationTesting setValidationRules(ValidationRule validationRule){
        this.validationRules.put(validationRule.getParamName(), validationRule);
        return this;
    }


}
