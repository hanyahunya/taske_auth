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
    String to;
    String subject;
    String templateName;
    Map<String, String> variables;
    String locale;
}
