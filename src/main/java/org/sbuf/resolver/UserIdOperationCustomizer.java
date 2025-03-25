package org.sbuf.resolver;

import org.springframework.stereotype.Component;

@Component
public class UserIdOperationCustomizer extends AbstractOperationCustomizer<UserId> {
    protected UserIdOperationCustomizer() {
        super(UserId.class, (annotation) -> new OperationDetail(annotation.value(), annotation.required(), annotation.type(), annotation.description(), null));
    }
}
