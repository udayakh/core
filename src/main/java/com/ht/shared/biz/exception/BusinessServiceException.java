package com.ht.shared.biz.exception;

/**
 * Exception class meant to be throw by service impls. It's meant primarily as a validation or
 * illegal-argument exception. It might also be thrown if a complex operation needs to be rolled
 * back.
 *
 */
public class BusinessServiceException extends Exception {

  private static final long serialVersionUID = 1L;

  public BusinessServiceException() {
    super();
  }

  public BusinessServiceException(String msg, Throwable exception) {
    super(msg, exception);
  }

  public BusinessServiceException(Throwable exception) {
    super(exception);
  }

  public BusinessServiceException(String message) {
    super(message);
  }

}
