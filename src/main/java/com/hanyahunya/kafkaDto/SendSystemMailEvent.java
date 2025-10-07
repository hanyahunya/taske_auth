package com.hanyahunya.kafkaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendSystemMailEvent {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, String> variables;
    private String locale;
}
