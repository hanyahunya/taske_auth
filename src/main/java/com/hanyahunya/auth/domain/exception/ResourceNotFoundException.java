package com.hanyahunya.auth.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String resourceName, Object resourceId) {
    super(String.format("%s with id '%s' not found", resourceName, resourceId.toString()));
  }
}
