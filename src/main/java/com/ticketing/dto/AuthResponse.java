package com.ticketing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String message;
    private boolean success;
}
