package com.hanyahunya.auth.encoding;

public interface EncodeService {
    String encode(String data);
    boolean matches(String data, String hashedData);
}