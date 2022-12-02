package edu.kmaooad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.dto.BotUpdateResult;
import edu.kmaooad.exception.EmptyRequestBodyException;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public class TelegramWebhookHandler extends FunctionInvoker<Update, BotUpdateResult> {


    @FunctionName("TelegramWebhook")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        Update update;
        BotUpdateResult result;

        try {
            final String body = request
                    .getBody()
                    .orElseThrow(EmptyRequestBodyException::new);

            final ObjectMapper mapper = new ObjectMapper();
            update = mapper.readValue(body, Update.class);
            result = handleRequest(update, context);
        } catch (Exception exception) {
            result = BotUpdateResult.Error(exception.getMessage());
        }

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
