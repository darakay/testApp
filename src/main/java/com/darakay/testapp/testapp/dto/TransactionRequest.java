package com.darakay.testapp.testapp.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode
@Getter
@JsonAutoDetect
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @JsonProperty("sum")
    private double sum;
    @JsonProperty("sourceAccountId")
    private long sourceAccountId;
    @JsonProperty("targetAccountId")
    private long targetAccountId;
}
