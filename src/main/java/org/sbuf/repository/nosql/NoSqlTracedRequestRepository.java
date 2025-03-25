package org.sbuf.repository.nosql;

import org.bson.types.ObjectId;
import org.sbuf.model.entity.nosql.NoSqlTracedRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoSqlTracedRequestRepository extends MongoRepository<NoSqlTracedRequest, String> {

    NoSqlTracedRequest findByTransactionId(String transactionId);
}
