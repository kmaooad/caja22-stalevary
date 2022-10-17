package edu.kmaooad;

import edu.kmaooad.exception.DatabaseConnectionException;
import edu.kmaooad.exception.EmptyRequestBodyException;
import edu.kmaooad.exception.InvalidOperationException;
import edu.kmaooad.repository.MessageRepository;
import edu.kmaooad.repository.MessageRepositoryImpl;
import org.json.JSONException;
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

        JSONObject messageJson;
        int messageId;

        try {
            String body = request.getBody().orElseThrow(EmptyRequestBodyException::new);

            JSONObject bodyJson = new JSONObject(body);
            messageJson = bodyJson.getJSONObject("message");
            messageId = messageJson.getInt("message_id");
        } catch (EmptyRequestBodyException | JSONException exception) {

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage())
                    .build();
        }

        try {
            MessageRepository messageRepository = new MessageRepositoryImpl(MONGO_URL, DATABASE, COLLECTION);
            messageRepository.addMessage(messageJson);
        } catch (InvalidOperationException | DatabaseConnectionException exception) {

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage())
                    .build();
        }

        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(messageId)
                .build();
    }
}
