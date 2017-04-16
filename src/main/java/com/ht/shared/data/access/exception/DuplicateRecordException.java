package com.ht.shared.data.access.exception;

public class DuplicateRecordException extends DataSourceOperationFailedException {

  private static final long serialVersionUID = 1L;

  public DuplicateRecordException() {
    super();
  }

  public DuplicateRecordException(String message) {
    super(message);
  }

  public DuplicateRecordException(Throwable exception) {
    super(exception);
  }

  public DuplicateRecordException(String message, Throwable exception) {
    super(message, exception);
  }
}
