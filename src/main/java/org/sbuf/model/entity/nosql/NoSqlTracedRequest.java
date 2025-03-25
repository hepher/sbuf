package org.sbuf.model.entity.nosql;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.sbuf.model.entity.TracedRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Document("trace_request")
public class NoSqlTracedRequest implements TracedRequest {

    @Id
    private String id;

    @Field("transaction_id")
    private String transactionId;

    @Indexed(expireAfter = "60d")
    @Field("insert_datetime")
    private Date insertDateTime;

    @Field("path")
    private String path;

    @Field("method")
    private String method;

    @Field("http_method")
    private String httpMethod;

    @Field("header")
    private String header;

    @Field("request_body")
    private String requestBody;

    @Field("response_body")
    private Object responseBody;

    @Field("response_status")
    private Integer responseStatus;

    @Field("error_code")
    private String errorCode;
}
