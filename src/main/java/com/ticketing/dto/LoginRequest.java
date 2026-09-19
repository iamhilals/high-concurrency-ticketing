package com.ticketing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
