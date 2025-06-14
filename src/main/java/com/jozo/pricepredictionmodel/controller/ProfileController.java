package com.jozo.pricepredictionmodel.controller;

import com.jozo.pricepredictionmodel.DTO.UpdateEmailRequest;
import com.jozo.pricepredictionmodel.DTO.UpdateFirstNameRequest;
import com.jozo.pricepredictionmodel.DTO.UpdateLastNameRequest;
import com.jozo.pricepredictionmodel.DTO.UserProfileDTO;
import com.jozo.pricepredictionmodel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getUserProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            UserProfileDTO userProfile = userService.getUserProfile(userEmail);
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException e) {
            System.err.println("Error fetching user profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/firstName")
    public ResponseEntity<String> updateFirstName(@RequestBody UpdateFirstNameRequest request, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userService.updateFirstName(userEmail, request.getFirstName());
            return ResponseEntity.ok("First name updated successfully!");
        } catch (RuntimeException e) {
            System.err.println("Error updating first name: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/lastName")
    public ResponseEntity<String> updateLastName(@RequestBody UpdateLastNameRequest request, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userService.updateLastName(userEmail, request.getLastName());
            return ResponseEntity.ok("Last name updated successfully!");
        } catch (RuntimeException e) {
            System.err.println("Error updating last name: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/email")
    public ResponseEntity<String> updateEmail(@RequestBody UpdateEmailRequest request, Authentication authentication) {
        try {
            String oldEmail = authentication.getName();
            userService.updateEmail(oldEmail, request.getEmail());
            return ResponseEntity.ok("Email updated successfully! Please re-login with your new email.");
        } catch (IllegalArgumentException e) {
            System.err.println("Email update validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error updating email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating email: " + e.getMessage());
        }
    }
}