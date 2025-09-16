package com.hanyahunya.auth.application.port.out;

public interface EncodeServicePort {
    String encode(String data);
    boolean matches(String data, String hashedData);
}