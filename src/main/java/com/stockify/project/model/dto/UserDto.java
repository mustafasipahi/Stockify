package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Long tenantId;
}
