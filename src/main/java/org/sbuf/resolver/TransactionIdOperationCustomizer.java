package org.sbuf.resolver;

import org.springframework.stereotype.Component;

@Component
public class TransactionIdOperationCustomizer extends AbstractOperationCustomizer<TransactionId> {

    public TransactionIdOperationCustomizer() {
        super(TransactionId.class, (annotation) -> new OperationDetail(annotation.value(), annotation.required(), annotation.type(), annotation.description(), null));
    }
}
