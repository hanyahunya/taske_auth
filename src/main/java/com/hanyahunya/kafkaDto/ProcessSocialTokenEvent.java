package com.hanyahunya.kafkaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSocialTokenEvent {
    private String provider;
    private String userSub;
    private String authorizationCode;
}
