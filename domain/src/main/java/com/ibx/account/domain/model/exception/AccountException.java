package com.ibx.account.domain.model.exception;

import com.ibx.account.domain.model.errors.ErrorsEnum;
import lombok.Getter;

@Getter
public class AccountException extends RuntimeException {

  private final ErrorsEnum errorsEnum;

  private final String[] params;

  public AccountException(ErrorsEnum errorsEnum, String... params) {
    super(errorsEnum.getMessage());
    this.errorsEnum = errorsEnum;
    this.params = params;
  }

}
