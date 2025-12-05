package com.enelx.bfw.framework.interceptor;

import com.enelx.bfw.framework.main.AbstractGrpcInterceptor;
import com.enelx.bfw.framework.util.ApplicationContextUtils;
import com.enelx.bfw.framework.util.LabelUtils;
import com.google.protobuf.MessageOrBuilder;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@GrpcGlobalClientInterceptor
public class GrpcClientInterceptor extends AbstractGrpcInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        StopWatch stopWatch = new StopWatch();
        GrpcLoggingRequestData<ReqT, RespT> loggingRequest = new GrpcLoggingRequestData<>();

        loggingRequest.setChannel(next.authority());
        loggingRequest.setServiceName(parseServiceName(method));
        loggingRequest.setMethod(method.getBareMethodName());

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(getMetadataKey(), ApplicationContextUtils.getTransactionId());
                loggingRequest.setRequestHeaders(headers);

                stopWatch.start();
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {

                    @Override
                    public void onHeaders(Metadata headers) {
                        loggingRequest.setResponseHeaders(headers);
                        super.onHeaders(headers);
                    }

                    @Override
                    public void onMessage(RespT message) {
                        loggingRequest.setResponse(message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        stopWatch.stop();

                        log.info(LabelUtils.LOG_GRPC_RESPONSE,
                                loggingRequest.getChannel(),
                                loggingRequest.getServiceName(),
                                loggingRequest.getMethod(),
                                loggingRequest.getRequestHeaders(),
                                formatMessageToJson((MessageOrBuilder) loggingRequest.getRequest()),
                                stopWatch.getTotalTimeMillis() + "ms",
                                status,
                                loggingRequest.getResponseHeaders(),
                                formatMessageToJson((MessageOrBuilder) loggingRequest.getResponse())
                        );
                        
                        super.onClose(status, trailers);
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                loggingRequest.setRequest(message);

                log.info(LabelUtils.LOG_GRPC_REQUEST,
                        loggingRequest.getChannel(),
                        loggingRequest.getServiceName(),
                        loggingRequest.getMethod(),
                        loggingRequest.getRequestHeaders(),
                        formatMessageToJson((MessageOrBuilder) message)
                );

                super.sendMessage(message);
            }
        };
    }
}
