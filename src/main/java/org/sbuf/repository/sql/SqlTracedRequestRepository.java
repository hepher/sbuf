package org.sbuf.repository.sql;

import org.sbuf.model.entity.sql.SqlTracedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlTracedRequestRepository extends JpaRepository<SqlTracedRequest, Integer> {
    SqlTracedRequest findByTransactionId(String transactionId);
}
