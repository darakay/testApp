package com.darakay.testapp.testapp.dto;

import com.darakay.testapp.testapp.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonAutoDetect
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class UserTransactionDto {
    @JsonProperty("accountId")
    private long accountId;

    @JsonProperty("otherId")
    private long otherId;

    @JsonProperty("sum")
    private double sum;

    @JsonProperty("date")
    private String date;

    @JsonProperty("type")
    private String type;

    private UserTransactionDto(long accountId, long otherId, double sum, String date, String type) {
        this.accountId = accountId;
        this.otherId = otherId;
        this.sum = sum;
        this.date = date;
        this.type = type;
    }

    public UserTransactionDto() {
    }

    public static UserTransactionDto fromTransaction(Transaction transaction){
        return new UserTransactionDto(transaction.getSource().getId(),
                transaction.getTarget().getId(), transaction.getSum(),
                transaction.getDate() + " " + transaction.getTime(),
                transaction.getType());
    }
}
