import com.enel.notification.grpc.MessageServiceGrpc;
import com.enel.notification.grpc.WelcomeRequest;
import com.enel.notification.grpc.WelcomeResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class MessageClient {

    @GrpcClient("message-service") // This name matches the client configuration in application.properties
    private MessageServiceGrpc.MessageServiceBlockingStub messageStub;

    public String sendMessage(String name) {
        WelcomeRequest request = WelcomeRequest.newBuilder()
                .setName(name)
                .build();
        WelcomeResponse response = messageStub.welcome(request);

        return response.getMessage();
    }
}
