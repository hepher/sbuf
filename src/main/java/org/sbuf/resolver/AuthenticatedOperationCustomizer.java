package org.sbuf.resolver;

import org.sbuf.security.validation.Authenticated;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class AuthenticatedOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {

        Jwt jwt = AnnotationUtils.synthesizeAnnotation(Jwt.class);
        if (handlerMethod.hasMethodAnnotation(Authenticated.class)) {
            Parameter parameter = new Parameter();
            parameter.setName(jwt.value());
            parameter.setRequired(true);
            parameter.setIn(jwt.type().getValue());
            parameter.setDescription("JWT for user request");
            operation.addParametersItem(parameter);
        }

        return operation;
    }
}
