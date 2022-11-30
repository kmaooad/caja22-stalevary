package edu.kmaooad.core.filters;

import org.telegram.telegrambots.meta.api.objects.Message;

record TextFilter(String text) implements Filter {

    @Override
    public boolean isApplicable(Message message) {
        return message.getText().equalsIgnoreCase(text);
    }
}