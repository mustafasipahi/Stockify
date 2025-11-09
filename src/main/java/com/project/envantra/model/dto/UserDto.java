package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long profileImageId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String tkn;
    private String vkn;
    private Role role;
}
