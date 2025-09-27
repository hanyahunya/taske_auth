package com.hanyahunya.kafkaDto;

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
}
