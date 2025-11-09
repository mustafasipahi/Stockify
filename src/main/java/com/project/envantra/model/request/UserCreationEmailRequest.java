package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationEmailRequest {

    private String brokerUsername;
    private String brokerPassword;
    private String brokerFirstName;
    private String brokerLastName;
    private String creatorUserFirstName;
    private String creatorUserLastName;
}
