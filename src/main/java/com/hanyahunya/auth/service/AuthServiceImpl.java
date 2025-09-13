package com.hanyahunya.auth.service;

import com.hanyahunya.auth.dto.SignupDto;
import com.hanyahunya.auth.encoding.EncodeService;
import com.hanyahunya.auth.entity.Role;
import com.hanyahunya.auth.entity.User;
import com.hanyahunya.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EncodeService encodeService;
    private final UserRepository userRepository;

    @Override
    public boolean signUp(SignupDto signupDto) {
        UUID uuid = UUID.randomUUID();
        // redisにデータ移動後、認証の後処理
        User user = User.builder()
                .userId(uuid)
                .email(signupDto.getEmail())
                .password(encodeService.encode(signupDto.getPassword()))
                .role(Role.USER)
                .build();
        // ｇRPCで確認メール発送
        userRepository.save(user);
        return false;
    }
}
