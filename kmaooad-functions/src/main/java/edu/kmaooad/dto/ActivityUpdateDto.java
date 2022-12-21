package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpdateDto {
    @JsonProperty("activity_id")
    private String activityId;

    @JsonProperty("field")
    private String field;

    @JsonProperty("value")
    private String value;
}
