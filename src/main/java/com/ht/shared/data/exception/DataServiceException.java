package com.ht.shared.data.exception;

public class DataServiceException extends Exception {

  private static final long serialVersionUID = 1L;

  public DataServiceException() {
    super();
  }

  public DataServiceException(String msg, Throwable exception) {
    super(msg, exception);
  }

  public DataServiceException(Throwable exception) {
    super(exception);
  }

  public DataServiceException(String message) {
    super(message);
  }

}
