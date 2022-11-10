package edu.kmaooad.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
public class BotUpdate {

    @MongoId
    private String messageId;
    
    private String errorMessage;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
