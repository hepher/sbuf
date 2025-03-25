package org.sbuf.resolver;

import org.springframework.stereotype.Component;

@Component
public class UserIpOperationCustomizer extends AbstractOperationCustomizer<UserIp> {

    public UserIpOperationCustomizer() {
        super(UserIp.class, (annotation) -> null);
    }
}
