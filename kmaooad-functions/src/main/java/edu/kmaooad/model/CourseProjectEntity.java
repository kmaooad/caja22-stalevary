package edu.kmaooad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class CourseProjectEntity {

    @Id
    private String id;

    @Field("project_title")
    private String title;

    @Field("project_description")
    private String description;

    @Field("project_requirements")
    private String requirements;

    @Field("project_deadline")
    private String time;
}
