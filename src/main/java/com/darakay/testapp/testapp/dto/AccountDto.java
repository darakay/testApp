package com.darakay.testapp.testapp.dto;

import com.darakay.testapp.testapp.entity.Account;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonAutoDetect
@EqualsAndHashCode
public class AccountDto {
    @JsonProperty("sum")
    private double sum;
    @JsonProperty("ownerId")
    private long ownerId;
    @JsonProperty("tariffName")
    private String tariffName;

    public static AccountDto fromEntity(Account account){
        return new AccountDto(account.getSum(), account.getOwner().getId(), account.getTariff().getName());
    }
}
