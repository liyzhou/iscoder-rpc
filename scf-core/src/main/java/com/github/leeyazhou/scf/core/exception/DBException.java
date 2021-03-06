package com.github.leeyazhou.scf.core.exception;

public class DBException extends SCFException {
  private static final long serialVersionUID = 1L;

  public DBException() {
    super("服务器端数据库错误!");
  }

  public DBException(String message) {
    super(message);
    this.setErrCode(ReturnType.DB);
  }
}
