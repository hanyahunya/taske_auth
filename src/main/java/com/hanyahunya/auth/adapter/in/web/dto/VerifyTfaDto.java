package com.hanyahunya.auth.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VerifyTfaDto {
    @NotBlank
    @Pattern(regexp = "\\d{6}")
    @JsonProperty("code")
    private String validateCode;
}
