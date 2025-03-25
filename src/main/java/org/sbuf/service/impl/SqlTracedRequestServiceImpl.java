package org.sbuf.service.impl;

import org.sbuf.exception.SbufException;
import org.sbuf.model.entity.TracedRequest;
import org.sbuf.model.entity.sql.SqlTracedRequest;
import org.sbuf.repository.sql.SqlTracedRequestRepository;
import org.sbuf.service.SqlAbstractService;
import org.sbuf.service.TracedRequestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnExpression("${sbuf.config.tracing.on-sql:false}")
public class SqlTracedRequestServiceImpl extends SqlAbstractService<SqlTracedRequest, Integer, SqlTracedRequestRepository> implements TracedRequestService {

    @Override
    public TracedRequest save(TracedRequest entity) {
        if (entity instanceof SqlTracedRequest) {
            return repository.save((SqlTracedRequest) entity);
        }

        throw new SbufException("Invalid entity");
    }

    @Override
    public TracedRequest buildEntity() {
        return new SqlTracedRequest();
    }

    @Override
    @Transactional(readOnly = true)
    public TracedRequest findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }
}
