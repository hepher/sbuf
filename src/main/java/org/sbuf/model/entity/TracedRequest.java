package org.sbuf.model.entity;

import java.util.Date;

public interface TracedRequest {

    String getTransactionId();

    void setTransactionId(String transactionId);

    Date getInsertDateTime();

    void setInsertDateTime(Date insertDateTime);

    String getPath();

    void setPath(String path);

    String getMethod();

    void setMethod(String method);

    String getHttpMethod();

    void setHttpMethod(String httpMethod);

    String getHeader();

    void setHeader(String header);

    String getRequestBody();

    void setRequestBody(String requestBody);

    Object getResponseBody();

    void setResponseBody(Object responseBody);

    Integer getResponseStatus();

    void setResponseStatus(Integer responseStatus);

    String getErrorCode();

    void setErrorCode(String errorCode);
}
