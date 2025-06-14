package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.DTO.UserProfileDTO;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserProfileDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return new UserProfileDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public void updateFirstName(String email, String newFirstName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        user.setFirstName(newFirstName);
        userRepository.save(user);
    }

    public void updateLastName(String email, String newLastName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        user.setLastName(newLastName);
        userRepository.save(user);
    }

    public void updateEmail(String oldEmail, String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid new email format.");
        }
        if (userRepository.findByEmail(newEmail).isPresent() && !newEmail.equalsIgnoreCase(oldEmail)) {
            throw new IllegalArgumentException("Email already taken by another user.");
        }

        User user = userRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + oldEmail));

        user.setEmail(newEmail.trim());
        userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }
}