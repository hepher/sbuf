package org.sbuf.security.validation.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.sbuf.util.LabelUtils;
import org.springframework.web.method.HandlerMethod;

public class JwtSecurityRequestAuthenticator extends AbstractJwtSecurityRequest {
    @Override
    public boolean authenticate(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        String authorizationHeader = request.getHeader(LabelUtils.AUTHENTICATION_HEADER);
        if (StringUtils.isBlank(authorizationHeader)) {
            return false;
        }

        return checkJwt(authorizationHeader.replaceFirst("^Bearer ", ""));
    }

    @Override
    public String unauthorizedMessage() {
        return "HTTP Status 401: Missing or invalid jwt or uniqueID";
    }
}
