package com.darakay.testapp.testapp.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class AccountCreateRequestDto {
    private String tariffName;
}
