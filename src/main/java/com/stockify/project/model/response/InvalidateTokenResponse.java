package com.stockify.project.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvalidateTokenResponse {

    private Boolean success;
    private String message;
}
