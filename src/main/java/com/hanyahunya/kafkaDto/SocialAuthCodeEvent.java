package com.hanyahunya.kafkaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialAuthCodeEvent {
    private String provider;
    private String authorizationCode;
}
