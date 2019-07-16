package com.darakay.testapp.testapp.dto;

import com.darakay.testapp.testapp.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@JsonAutoDetect
@Data
public class TransactionDto {
    @JsonProperty("sourceId")
    private long sourceId;
    @JsonProperty("targetId")
    private long targetId;
    @JsonProperty("sum")
    private double sum;
    @JsonProperty("id")
    private long id;
    @JsonProperty("userId")
    private long userId;

    private TransactionDto(long userId, long sourceId, long targetId, double sum, long id) {
        this.userId = userId;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sum = sum;
        this.id = id;
    }

    public TransactionDto() {
    }

    public static TransactionDto fromEntity(Transaction transaction){
        return new TransactionDto(transaction.getUser().getId(), transaction.getSource().getId(),
                transaction.getTarget().getId(), transaction.getSum(), transaction.getId());
    }
}
