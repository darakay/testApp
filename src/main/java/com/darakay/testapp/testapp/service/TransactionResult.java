package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonAutoDetect
@EqualsAndHashCode
@ToString
public class TransactionResult {
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("transactionId")
    @Getter
    private long transactionId;

    private TransactionResult(String errorMessage, long transactionId) {
        this.errorMessage = errorMessage;
        this.transactionId = transactionId;
    }

    public TransactionResult() {}

    static TransactionResult invalidSum(double sum) {
        return new TransactionResult("Invalid sum", -1);
    }

    static TransactionResult ok(Transaction savedTransaction) {
        return new TransactionResult(null, savedTransaction.getId());
    }

    @JsonIgnore
    public boolean isSuccess(){
        return errorMessage == null;
    }
}
