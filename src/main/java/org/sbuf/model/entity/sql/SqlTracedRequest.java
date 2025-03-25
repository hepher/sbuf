package org.sbuf.model.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.sbuf.model.entity.TracedRequest;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "trace_request")
public class SqlTracedRequest implements TracedRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "insert_datetime")
    private Date insertDateTime;

    @Column(name = "path")
    private String path;

    @Column(name = "method")
    private String method;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "header")
    private String header;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "response_body")
    private Object responseBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "error_code")
    private String errorCode;
}
