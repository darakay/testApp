package com.darakay.testapp.testapp.dto;

import com.darakay.testapp.testapp.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonAutoDetect
@EqualsAndHashCode
@ToString
public class UserDto {
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("id")
    private long id;

    public UserDto() {
    }

    public UserDto(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static UserDto fromEntity(User user){
       return new UserDto(user.getId(), user.getFirstName(), user.getLastName());
    }
}
