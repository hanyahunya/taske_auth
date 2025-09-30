package com.hanyahunya.auth.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD}) // 이 어노테이션을 어디에 붙일수 있는지 지정.
@Retention(RUNTIME) // 이 어노테이션이 언제까지 유지될지.
@Constraint(validatedBy = SupportedLocaleValidator.class) // 이 어노테이션의 유효성 검사를 어떤 클래스가 담당하는지
public @interface SupportedLocale {
    // 유효성 검사 실패 시 보여줄 기본 메시지
    String message() default "Unsupported locale.";

    //-------이 밑에는 나중에 어노테이션 더 알고싶을때 공부. 일단은 기본값--------
    // 유효성 검사 그룹을 지정 (기본값: 비어있음)
    Class<?>[] groups() default {};

    // 유효성 검사에 대한 심각도 등을 표현 (기본값: 비어있음)
    Class<? extends Payload>[] payload() default {};
}