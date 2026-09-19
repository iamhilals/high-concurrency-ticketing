package com.ticketing.service;

import com.ticketing.dto.AuthResponse;
import com.ticketing.dto.LoginRequest;
import com.ticketing.dto.RegisterRequest;
import com.ticketing.entity.User;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return AuthResponse.builder().success(false).message("Kullanıcı adı boş olamaz.").build();
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return AuthResponse.builder().success(false).message("E-posta adresi boş olamaz.").build();
        }
        if (request.getPassword() == null || request.getPassword().length() < 3) {
            return AuthResponse.builder().success(false).message("Şifre en az 3 karakter olmalıdır.").build();
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthResponse.builder().success(false).message("Bu kullanıcı adı zaten alınmış.").build();
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthResponse.builder().success(false).message("Bu e-posta adresi zaten kayıtlı.").build();
        }

        User newUser = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(newUser);

        return AuthResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .message("Kayıt işlemi başarıyla tamamlandı!")
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().isBlank()) {
            return AuthResponse.builder().success(false).message("Kullanıcı adı veya e-posta giriniz.").build();
        }

        String input = request.getUsernameOrEmail().trim();
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(input, input.toLowerCase());

        if (userOpt.isEmpty()) {
            return AuthResponse.builder().success(false).message("Kullanıcı bulunamadı.").build();
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return AuthResponse.builder().success(false).message("Hatalı şifre!").build();
        }

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .message("Giriş başarılı!")
                .success(true)
                .build();
    }
}
