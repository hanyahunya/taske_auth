package com.hanyahunya.auth.application.security;

import com.hanyahunya.auth.domain.model.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IdTokenValidatorFactory {
    private final Map<Provider, IdTokenValidator> idTokenValidator;

    public IdTokenValidatorFactory(List<IdTokenValidator> validators) {
        this.idTokenValidator = validators.stream()
                .collect(Collectors.toMap(
                        IdTokenValidator::getProvider,
                        validator -> validator
                ));
    }

    public IdTokenValidator getIdTokenValidator(Provider provider) {
        IdTokenValidator validator = idTokenValidator.get(provider);
        if (validator == null) {
            throw new IllegalArgumentException("unsupported provider type: " + provider);
        }
        return validator;
    }
}
