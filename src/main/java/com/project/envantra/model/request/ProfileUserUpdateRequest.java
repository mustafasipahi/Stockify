package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUserUpdateRequest {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String tkn;
    private String vkn;
}
