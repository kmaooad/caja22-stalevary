package edu.kmaooad;

import org.json.JSONObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    private static final String MONGO_URL = System.getenv("MONGO_URL");
    private static final String DATABASE = "caja22-stalevary";
    private static final String COLLECTION = "messages";

    /**
     * This function listens at endpoint "/api/TelegramWebhook". To invoke it using "curl" command in bash:
     * curl -d "HTTP Body" {your host}/api/TelegramWebhook
     */
    @FunctionName("TelegramWebhook")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        MessageRepository messageRepository = new MessageRepository(MONGO_URL, DATABASE, COLLECTION);

        if (request.getBody().isEmpty())
            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .build();

        JSONObject obj = new JSONObject(request.getBody().get());
        JSONObject msg = obj.getJSONObject("message");
        messageRepository.addMessage(msg);
        int msgId = msg.getInt("message_id");

        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(msgId)
                .build();
    }
}
