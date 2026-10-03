package com.shop.common.exception;

public class EmailAlreadyUsedException extends RuntimeException {

  public EmailAlreadyUsedException(String email) {
    super("An account already exists with email: " + email);
  }
}
