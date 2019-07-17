package com.darakay.testapp.testapp.dto;

import com.darakay.testapp.testapp.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserInfo {
    private long id;
    private String firstName;
    private String secondName;
    private String login;
    private List<AccountDto> availableAccounts;
    protected List<AccountDto> ownAccounts;

    public static UserInfo fromEntity(User user){
        return new UserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getLogin(),
                user.getAccounts().stream().map(AccountDto::fromEntity).collect(Collectors.toList()),
                user.getOwnAccounts().stream().map(AccountDto::fromEntity).collect(Collectors.toList()));
    }
}
