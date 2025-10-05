package com.hanyahunya.kafkaDto;

import com.hanyahunya.auth.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedUpEvent {
    private UUID userId;
    private String email;
    private String country;
    private LocalDateTime signedUpAt;

    public static UserSignedUpEvent fromUser(User user) {
        return UserSignedUpEvent.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .country(user.getCountry())
                .signedUpAt(user.getCreatedAt())
                .build();
    }
}
