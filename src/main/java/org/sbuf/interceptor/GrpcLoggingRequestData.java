package com.enel.notification.commons.interceptor;

import io.grpc.Metadata;
import lombok.Data;

@Data
public class GrpcLoggingRequestData<ReqT, RespT> {

    private String channel;
    private String method;
    private String serviceName;
    private Metadata requestHeaders;
    private ReqT request;
    private Metadata responseHeaders;
    private RespT response;
}
