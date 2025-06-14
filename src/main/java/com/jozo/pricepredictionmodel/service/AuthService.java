package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.DTO.AuthResponse;
import com.jozo.pricepredictionmodel.DTO.LoginRequest;
import com.jozo.pricepredictionmodel.DTO.RegisterRequest;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import com.jozo.pricepredictionmodel.security.CustomUserDetails;
import com.jozo.pricepredictionmodel.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtil jwtService;
    @Autowired
    AuthenticationManager authenticationManager;

    public void register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen.")); // Ovo se ne bi trebalo dogoditi ako je autentifikacija uspjela

        UserDetails userDetails = new CustomUserDetails(user);

        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, user.getFirstName(), user.getLastName(), user.getEmail());
    }
}