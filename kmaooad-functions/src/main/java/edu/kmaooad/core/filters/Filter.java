package edu.kmaooad.core.filters;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface Filter {

    boolean isApplicable(Message message);
}
