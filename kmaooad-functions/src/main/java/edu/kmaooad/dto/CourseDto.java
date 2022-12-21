package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    @JsonProperty("title")
    @CsvBindByName
    private String title;

    @JsonProperty("description")
    @CsvBindByName
    private String description;
}
