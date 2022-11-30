package edu.kmaooad.core.session;

import java.util.Map;

public record UserSession(
        Long id,
        String currentState,

        Map<String, String> statesPayload
) {
}
