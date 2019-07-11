package com.darakay.testapp.testapp.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@EqualsAndHashCode
@ToString
@JsonAutoDetect
@Builder
@Data
public class TransactionDto {
    @JsonProperty("sourceId")
    private long sourceId;
    @JsonProperty("targetId")
    private long targetId;
    @JsonProperty("sum")
    private double sum;

    public TransactionDto(long sourceId, long targetId, double sum) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sum = sum;
    }

    public TransactionDto() {
    }
}
