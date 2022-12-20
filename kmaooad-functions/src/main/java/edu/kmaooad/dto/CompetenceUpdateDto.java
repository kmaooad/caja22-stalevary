package edu.kmaooad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kmaooad.model.ActivityEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceUpdateDto {
    @JsonProperty("activity")
    private ActivityUpdateDto activityUpdateDto;

    @JsonProperty("competence")
    private String competence;

    @JsonProperty("entity")
    private ActivityEntity entity;
}
