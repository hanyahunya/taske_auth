package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.application.port.out.ProcessSocialAuthPort;
import com.hanyahunya.auth.domain.model.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SocialLoginAdapterFactory {

    private final Map<Provider, ProcessSocialAuthPort> adapters;

    /*
        !?!?
        Constructor ( List<Port> ) 면
        생성자의 파라미터가 List<ProcessSocialAuthPort>인 것을 보고, 스프링은 자신이 관리하는 모든 빈 중에서 ProcessSocialAuthPort 인터페이스를 구현한 클래스의 빈들을 전부 찾음.
     */
    public SocialLoginAdapterFactory(List<ProcessSocialAuthPort> processSocialAuthPorts) {
        /*
            위에서 주입된 모든 빈들을 (Adapters) stream 으로 하나씩 처리
            1. 1번 어댑터에서 getProvider 로 GOOGLE을 가져옴 (예시)
            2. 지금 돌아가는 스트림 요소를 Value로 설정 ( GrpcGoogleAuthAdapter )
            -> this.adapters 에 GOOGLE, GrpcGoogleAuthAdapter 이렇게 저장됨.
         */
        this.adapters = processSocialAuthPorts.stream()
                .collect(Collectors.toMap(
                        com.hanyahunya.auth.application.port.out.ProcessSocialAuthPort::getProvider,   // Map 의 Key 부분
                        port -> port       // Map 의 Value 부분
                ));
    }

    public ProcessSocialAuthPort getAdapter(Provider provider) {
        ProcessSocialAuthPort adapter = adapters.get(provider);
        if (adapter == null) {
            throw new IllegalArgumentException("unsupported provider type: " + provider);
        }
        return adapter;
    }
}