package edu.kmaooad;

public record BotUpdateResult(String messageId, String errorMessage) {

    public static BotUpdateResult Ok(String messageId) {
        return new BotUpdateResult(messageId, null);
    }

    public static BotUpdateResult Error(String errorMessage) {
        return new BotUpdateResult(null, errorMessage);
    }

}
