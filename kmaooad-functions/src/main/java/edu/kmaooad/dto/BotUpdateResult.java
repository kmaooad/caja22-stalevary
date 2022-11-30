package edu.kmaooad.dto;

//TODO: refactor to generic result wrapper
public record BotUpdateResult(Integer messageId, String errorMessage) {

    public static BotUpdateResult Ok(Integer messageId) {
        return new BotUpdateResult(messageId, null);
    }

    public static BotUpdateResult Error(String errorMessage) {
        return new BotUpdateResult(null, errorMessage);
    }
}
