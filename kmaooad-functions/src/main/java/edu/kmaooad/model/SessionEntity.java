package edu.kmaooad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class SessionEntity {

    @Id
    private Long userId;

    @Field("state")
    private String state;

    @Field("payload")
    private Map<String, String> payload = new HashMap<>();
}

