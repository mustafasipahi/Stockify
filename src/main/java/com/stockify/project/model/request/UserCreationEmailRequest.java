package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
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
