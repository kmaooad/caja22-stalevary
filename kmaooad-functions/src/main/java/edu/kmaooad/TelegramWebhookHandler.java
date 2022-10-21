package edu.kmaooad;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.exception.EmptyRequestBodyException;
import edu.kmaooad.repository.MessageRepository;
import edu.kmaooad.repository.MessageRepositoryImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class TelegramWebhookHandler extends FunctionInvoker<BotUpdate, BotUpdateResult> {
    private static final String MONGO_URL = "mongodb+srv://admin:Hi6EWgtnkBej3pcn@cluster0.rxe1xqc.mongodb.net/?retryWrites=true&w=majority";
    private static final String DATABASE = "caja22-stalevary";
    private static final String COLLECTION = "messages";

    /**
     * This function listens at endpoint "/api/TelegramWebhook". To invoke it using
     * "curl" command in bash:
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
        BotUpdate botUpdate = new BotUpdate();

        try {
            String body = request.getBody().orElseThrow(EmptyRequestBodyException::new);

            JSONObject bodyJson = new JSONObject(body);
            messageJson = bodyJson.getJSONObject("message");
            botUpdate.setMessageId(
                    String.valueOf(messageJson.getInt("message_id"))
            );
        } catch (EmptyRequestBodyException | JSONException exception) {
            botUpdate.setErrorMessage(exception.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(handleRequest(botUpdate, context))
                    .build();
        }

        try {
            MessageRepository messageRepository = new MessageRepositoryImpl(MONGO_URL, DATABASE, COLLECTION);
            messageRepository.addMessage(messageJson);
        } catch (Exception exception) {
            botUpdate.setErrorMessage(exception.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(handleRequest(botUpdate, context))
                    .build();
        }

        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(handleRequest(botUpdate, context))
                .build();
    }
}
