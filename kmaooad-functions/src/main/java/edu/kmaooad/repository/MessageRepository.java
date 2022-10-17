package edu.kmaooad.repository;

import edu.kmaooad.exception.InvalidOperationException;
import org.json.JSONObject;

public interface MessageRepository {

    void addMessage(JSONObject message) throws InvalidOperationException;
}
