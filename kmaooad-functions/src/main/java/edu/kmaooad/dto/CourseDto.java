package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;
}
