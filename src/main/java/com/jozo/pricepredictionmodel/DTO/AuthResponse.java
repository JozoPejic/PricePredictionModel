package com.jozo.pricepredictionmodel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String jwtToken;
    private String firstName;
    private String lastName;
    private String email;
}