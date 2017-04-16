package com.ht.shared.data.access.exception;

public class DataAccessException extends Exception {

  private static final long serialVersionUID = 1L;

  public DataAccessException() {
    super();
  }

  public DataAccessException(String msg, Throwable exception) {
    super(msg, exception);
  }

  public DataAccessException(Throwable exception) {
    super(exception);
  }

  public DataAccessException(String message) {
    super(message);
  }

}
