package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AdminService {

    @Autowired
    UserRepository userRepository;

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
