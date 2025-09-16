package com.hanyahunya.auth.infra.grpc;

import email_service.EmailServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    // 반환 타입은 사용할 스텁의 타입(여기서는 BlockingStub)
    @Bean
    EmailServiceGrpc.EmailServiceBlockingStub emailServiceStub(GrpcChannelFactory channelFactory) {

        // "worker-service"라는 이름으로 채널을 생성
        // 이 이름이 properties 파일의 설정 key와 연결
        ManagedChannel channel = channelFactory.createChannel("worker-service");

        // 생성된 채널을 이용해 스텁을 만들고 Bean으로 반환
        return EmailServiceGrpc.newBlockingStub(channel);
    }
}
