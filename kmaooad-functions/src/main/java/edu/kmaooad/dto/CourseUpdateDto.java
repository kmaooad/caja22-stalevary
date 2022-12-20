package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDto {
    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("field")
    private String field;

    @JsonProperty("value")
    private String value;
}
