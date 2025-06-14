package com.jozo.pricepredictionmodel.controller;

import com.jozo.pricepredictionmodel.DTO.AuthResponse;
import com.jozo.pricepredictionmodel.DTO.LoginRequest;
import com.jozo.pricepredictionmodel.DTO.RegisterRequest;
import com.jozo.pricepredictionmodel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        try {
            authService.register(request);
            return ResponseEntity.ok("User registered successfully");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(authResponse);
        }
        catch(BadCredentialsException e){
            return ResponseEntity.status(401).body(new AuthResponse(null, null, null, null)); // 401 Unauthorized
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, null));
        }
    }

}
