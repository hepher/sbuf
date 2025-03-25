package org.sbuf.resolver;


import org.sbuf.exception.SbufException;
import org.sbuf.util.RequestParameterUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        UserId parameterAnnotation = methodParameter.getParameterAnnotation(UserId.class);

        if (parameterAnnotation == null) {
            return null;
        }

        String uniqueIdValue = (String) RequestParameterUtils.getValueFromRequest(request, parameterAnnotation.type(), parameterAnnotation.value());

        if (uniqueIdValue == null && parameterAnnotation.required()) {
            throw new SbufException("missing required parameter: " + parameterAnnotation.value(), HttpStatus.BAD_REQUEST.value() + "", HttpStatus.BAD_REQUEST, null);
        }

        return uniqueIdValue;
    }
}
