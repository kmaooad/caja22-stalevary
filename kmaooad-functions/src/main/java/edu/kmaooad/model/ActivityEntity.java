package edu.kmaooad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activities")
public class ActivityEntity {
    @Id
    private String id;

    @Field("owner_id")
    private String ownerId;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("date")
    private String date;

    @Field("time")
    private String time;

    @Field("location")
    private String location;

    @Field("competences")
    private List<String> competences;
}
