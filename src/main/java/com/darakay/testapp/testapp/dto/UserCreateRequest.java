package com.darakay.testapp.testapp.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class UserCreateRequest {
    private String firstName;
    private String lastName;
    private String login;
    private String password;
}
