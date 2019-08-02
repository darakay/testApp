package helpers;

import helpers.ValidationRule;
import helpers.ValidationTesting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class AbstractValidationTest {

    @Autowired
    protected MockMvc mockMvc;

    private ValidationTesting validationTesting;

    @Before
    public  void setUp(){
        this.validationTesting = setUpValidationTesting(new ValidationTesting());
    }

    @Test
    public void validationTest(){
        Method[] methods = validationTesting.getResource().getDeclaredMethods();
        Map<String, ValidationRule> validationFields = validationTesting.getValidationRules();
        List<Method> testingMethods = Stream.of(methods).filter(m ->
                Stream.of(m.getParameters()).anyMatch(p -> validationFields.containsKey(p.getName())))
                .collect(Collectors.toList());
        testingMethods.forEach(this::checkParameters);
    }

    @Test
    public void correctErrorResponseTest(){
        validationTesting.getValidationRules().forEach((name, rule) -> performRequest(rule));
    }

    private void checkParameters(Method method){
        Map<String, ValidationRule> validationFields = validationTesting.getValidationRules();
        Stream.of(method.getParameters())
                .filter(p -> validationFields.containsKey(p.getName()))
                .forEach(p ->
                    assertThat(Arrays.stream(p.getAnnotations())
                            .map(Annotation::annotationType)
                            .collect(Collectors.toList()))
                    .asList()
                    .containsSequence(validationFields.get(p.getName()).getValidatingAnnotations())
                );
    }

    private void performRequest(ValidationRule rule)  {
        try {
            mockMvc.perform(get(rule.getTestingUrl(),rule.getPathVars()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("description").value(rule.getErrorMessage()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    abstract ValidationTesting setUpValidationTesting(ValidationTesting validationTesting);
}
