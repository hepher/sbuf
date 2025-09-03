package com.enel.notification.commons.main;

import com.enel.notification.commons.util.LabelUtils;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;

public abstract class AbstractGrpcInterceptor {

    protected String formatMessageToJson(MessageOrBuilder message) {
        try {
            return JsonFormat.printer().includingDefaultValueFields().omittingInsignificantWhitespace().print(message);
        } catch (Exception e) {
            return message.toString(); // fallback
        }
    }

    protected  <ReqT, RespT> String parseServiceName(MethodDescriptor<ReqT, RespT> method) {
        if (method.getServiceName() == null) {
            return "";
        }
        String[] serviceNameParts = method.getServiceName().split("\\.");

        return serviceNameParts[serviceNameParts.length - 1];
    }

    protected  <ReqT, RespT> String parseServiceName(ServerCall<ReqT, RespT> call) {
        if (call.getMethodDescriptor().getServiceName() == null) {
            return "";
        }
        String[] serviceNameParts = call.getMethodDescriptor().getServiceName().split("\\.");

        return serviceNameParts[serviceNameParts.length - 1];
    }

    protected Metadata.Key<String> getMetadataKey() {
        return Metadata.Key.of(LabelUtils.ANNOTATION_TRANSACTION_ID, Metadata.ASCII_STRING_MARSHALLER);
    }
}
