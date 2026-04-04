package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.UserResponse;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findById(id));
    }

    public UserResponse updateRole(Long id, String role) {
        User user = findById(id);
        user.setRole(Role.valueOf(role.toUpperCase()));
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(Long id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        return toResponse(userRepository.save(user));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}