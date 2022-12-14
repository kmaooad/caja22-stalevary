package edu.kmaooad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class CourseEntity {

    @Id
    private String id;

    @Field("course_title")
    private String title;

    @Field("course_description")
    private String description;

    @Field("course_projects")
    private List<CourseProjectEntity> projects = new ArrayList<>();
}
