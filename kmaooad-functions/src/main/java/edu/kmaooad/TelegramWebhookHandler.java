package edu.kmaooad;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.dto.BotUpdate;
import edu.kmaooad.dto.BotUpdateResult;
import edu.kmaooad.exception.EmptyRequestBodyException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.Optional;

public class TelegramWebhookHandler extends FunctionInvoker<BotUpdate, BotUpdateResult> {

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

            String messageId = messageJson.getString("message_id");
            botUpdate.setMessageId(messageId);
        } catch (EmptyRequestBodyException | JSONException exception) {
            botUpdate.setErrorMessage(exception.getMessage());
        }

        BotUpdateResult result = handleRequest(botUpdate, context);

        if (result.errorMessage() == null)
            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .body(result.messageId())
                    .build();
        else
            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(result.errorMessage())
                    .build();
    }
}
