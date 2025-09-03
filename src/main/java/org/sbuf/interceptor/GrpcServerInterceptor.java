package com.enel.notification.commons.interceptor;

import com.enel.notification.commons.main.AbstractGrpcInterceptor;
import com.enel.notification.commons.util.ApplicationContextUtils;
import com.enel.notification.commons.util.LabelUtils;
import com.google.protobuf.MessageOrBuilder;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@GrpcGlobalServerInterceptor
public class GrpcServerInterceptor extends AbstractGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        StopWatch stopWatch = new StopWatch();
        GrpcLoggingRequestData<ReqT, RespT> loggingRequest = new GrpcLoggingRequestData<>();

        loggingRequest.setChannel(call.getAuthority());
        loggingRequest.setServiceName(parseServiceName(call));
        loggingRequest.setMethod(call.getMethodDescriptor().getBareMethodName());
        loggingRequest.setRequestHeaders(headers);

        ApplicationContextUtils.setTransactionId(headers.get(Metadata.Key.of(LabelUtils.ANNOTATION_TRANSACTION_ID, Metadata.ASCII_STRING_MARSHALLER)));

        // Wrappo la ServerCall per loggare le risposte
        ServerCall<ReqT, RespT> serverCall = new ForwardingServerCall.SimpleForwardingServerCall<>(call) {

            @Override
            public void sendMessage(RespT message) {
                loggingRequest.setResponse(message);
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                stopWatch.stop();
                log.info(LabelUtils.LOG_GRPC_RESPONSE,
                        loggingRequest.getChannel(),
                        loggingRequest.getServiceName(),
                        loggingRequest.getMethod(),
                        loggingRequest.getRequestHeaders(),
                        formatMessageToJson((MessageOrBuilder) loggingRequest.getRequest()),
                        stopWatch.getTotalTimeMillis() + "ms",
                        status,
                        trailers,
                        formatMessageToJson((MessageOrBuilder) loggingRequest.getResponse())
                    );
                super.close(status, trailers);
            }
        };

        stopWatch.start();
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
                next.startCall(serverCall, headers)) {

            @Override
            public void onMessage(ReqT message) {
                loggingRequest.setRequest(message);
                super.onMessage(message);
            }

            @Override
            public void onComplete() {
                log.info(LabelUtils.LOG_GRPC_REQUEST,
                        loggingRequest.getChannel(),
                        loggingRequest.getServiceName(),
                        loggingRequest.getMethod(),
                        loggingRequest.getRequestHeaders(),
                        formatMessageToJson((MessageOrBuilder) loggingRequest.getRequest())
                );
            }
        };
    }
}
