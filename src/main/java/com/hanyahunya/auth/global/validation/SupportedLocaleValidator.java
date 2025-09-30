package com.hanyahunya.auth.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class SupportedLocaleValidator implements ConstraintValidator<SupportedLocale, String> {

    private static final Set<String> SUPPORT_LOCALES = Set.of("ja-JP", "ko-KR", "en-US");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 값이 없을경우엔 우리가 처리안하고 다른 어노테이션한테 위임하겠다. @NotNull 같은거
        if (value == null || value.isEmpty()) {
            return true;
        }
        return SUPPORT_LOCALES.contains(value);
    }
}
