package com.ht.shared.biz.exception;

public class AuthenticationFailException extends BusinessServiceException {

  private static final long serialVersionUID = 1L;

  public AuthenticationFailException() {
    super();
  }

  public AuthenticationFailException(String msg, Throwable exception) {
    super(msg, exception);
  }

  public AuthenticationFailException(Throwable exception) {
    super(exception);
  }

  public AuthenticationFailException(String message) {
    super(message);
  }

}
