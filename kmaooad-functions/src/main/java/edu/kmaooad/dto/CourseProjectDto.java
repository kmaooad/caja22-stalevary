package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProjectDto {

    @JsonProperty
    @CsvBindByName
    private String title;

    @JsonProperty
    @CsvBindByName
    private String description;

    @JsonProperty
    @CsvBindByName
    private String requirements;

    @JsonProperty
    @CsvBindByName
    private String time;
}
