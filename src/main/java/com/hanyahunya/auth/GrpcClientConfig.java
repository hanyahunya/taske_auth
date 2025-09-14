package com.hanyahunya.auth;

import email_service.EmailServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
        // 반환 타입은 사용할 스텁의 타입(여기서는 BlockingStub)
    EmailServiceGrpc.EmailServiceBlockingStub emailServiceStub(GrpcChannelFactory channelFactory) {

        // 1. "worker-service"라는 이름으로 채널을 생성
        // 이 이름이 properties 파일의 설정 key와 연결
        ManagedChannel channel = channelFactory.createChannel("worker-service");

        // 2. 생성된 채널을 이용해 스텁을 만들고 Bean으로 반환
        return EmailServiceGrpc.newBlockingStub(channel);
    }
}
