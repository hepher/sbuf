package org.sbuf.service.impl;


import org.sbuf.exception.SbufException;
import org.sbuf.model.entity.TracedRequest;
import org.sbuf.model.entity.nosql.NoSqlTracedRequest;
import org.sbuf.repository.nosql.NoSqlTracedRequestRepository;
import org.sbuf.service.NoSqlAbstractService;
import org.sbuf.service.TracedRequestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnExpression("${sbuf.config.tracing.on-mongo:false}")
public class NoSqlTracedRequestServiceImpl extends NoSqlAbstractService<NoSqlTracedRequest, NoSqlTracedRequestRepository> implements TracedRequestService {

    @Override
    public TracedRequest save(TracedRequest entity) {
        if (entity instanceof NoSqlTracedRequest) {
            return repository.save((NoSqlTracedRequest) entity);
        }

        throw new SbufException("Invalid entity");
    }

    @Override
    public TracedRequest buildEntity() {
        return new NoSqlTracedRequest();
    }

    @Override
    @Transactional(readOnly = true)
    public TracedRequest findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }
}
