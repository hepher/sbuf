import com.enel.notification.grpc.MessageServiceGrpc;
import com.enel.notification.grpc.WelcomeRequest;
import com.enel.notification.grpc.WelcomeResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {

    @Override
    public void welcome(WelcomeRequest welcomeRequest, StreamObserver<WelcomeResponse> responseStreamObserver) {
        String name = welcomeRequest.getName();
        String message = "Hello, " + name + " from standalone gRPC Server!";
        WelcomeResponse reply = WelcomeResponse.newBuilder().setMessage(message).build();
        responseStreamObserver.onNext(reply);
        responseStreamObserver.onCompleted();
    }
}
