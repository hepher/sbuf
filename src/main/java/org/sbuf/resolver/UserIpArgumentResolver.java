package org.sbuf.resolver;

import org.sbuf.util.ApplicationContextUtils;
import org.sbuf.util.RequestParameterUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserIpArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(UserIp.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        UserIp parameterAnnotation = methodParameter.getParameterAnnotation(UserIp.class);

        String userIp = null;
        if (parameterAnnotation != null) {

            userIp = request.getHeader(parameterAnnotation.value());

            if (StringUtils.isBlank(userIp) && StringUtils.isNotBlank(String.valueOf(ApplicationContextUtils.evalExpression("${sbuf.resolver.user-ip.header-name}")))) {
                userIp = request.getHeader(String.valueOf(ApplicationContextUtils.evalExpression("${sbuf.resolver.user-ip.header-name}")));
            }

            if (StringUtils.isBlank(userIp)) {
                userIp = RequestParameterUtils.getUserIp(request);
            }
        }

        return userIp;
    }
}
