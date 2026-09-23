package com.ticketing.service;

import com.ticketing.dto.AuthResponse;
import com.ticketing.dto.RegisterRequest;
import com.ticketing.entity.User;
import com.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse updateUserProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userId));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            userRepository.findByUsername(request.getUsername().trim()).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new IllegalArgumentException("Bu kullanıcı adı başka bir hesap tarafından kullanılıyor.");
                }
            });
            user.setUsername(request.getUsername().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            userRepository.findByEmail(request.getEmail().trim().toLowerCase()).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new IllegalArgumentException("Bu e-posta adresi başka bir hesap tarafından kullanılıyor.");
                }
            });
            user.setEmail(request.getEmail().trim().toLowerCase());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 3) {
                throw new IllegalArgumentException("Şifre en az 3 karakter olmalıdır.");
            }
            user.setPassword(request.getPassword());
        }

        User updated = userRepository.save(user);

        return AuthResponse.builder()
                .id(updated.getId())
                .username(updated.getUsername())
                .email(updated.getEmail())
                .message("Profil bilgileri başarıyla güncellendi!")
                .success(true)
                .build();
    }
}
