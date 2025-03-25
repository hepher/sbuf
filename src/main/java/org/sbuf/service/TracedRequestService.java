package org.sbuf.service;

import org.sbuf.model.entity.TracedRequest;
import org.springframework.stereotype.Service;

@Service
public interface TracedRequestService {

    TracedRequest save(TracedRequest entity);
    TracedRequest buildEntity();

    TracedRequest findByTransactionId(String transactionId);
}
