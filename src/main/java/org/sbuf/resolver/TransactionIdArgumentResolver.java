package org.sbuf.resolver;


import org.sbuf.util.ApplicationContextUtils;
import org.sbuf.util.RequestParameterUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class TransactionIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(TransactionId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        TransactionId parameterAnnotation = methodParameter.getParameterAnnotation(TransactionId.class);

        String transactionId = null;
        if (parameterAnnotation != null) {
            transactionId = (String) RequestParameterUtils.getValueFromRequest(request, parameterAnnotation.type(), parameterAnnotation.value());
        }

        if (transactionId == null) {
            transactionId = ApplicationContextUtils.getTransactionId();
        }

        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }

        return transactionId;
    }
}
