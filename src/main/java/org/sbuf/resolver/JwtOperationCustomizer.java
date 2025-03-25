package org.sbuf.resolver;

import org.springframework.stereotype.Component;

@Component
public class JwtOperationCustomizer extends AbstractOperationCustomizer<Jwt> {

    protected JwtOperationCustomizer() {
        super(Jwt.class, (annotation) -> null);
    }
}
