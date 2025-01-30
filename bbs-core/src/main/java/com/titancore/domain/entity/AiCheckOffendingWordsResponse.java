package com.titancore.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiCheckOffendingWordsResponse {

    @JsonProperty("isViolation")
    private boolean isViolation;

    @JsonProperty("reason")
    private String reason;
}
