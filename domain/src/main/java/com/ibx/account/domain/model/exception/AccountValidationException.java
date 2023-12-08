package com.ibx.account.domain.model.exception;

import com.ibx.account.domain.model.errors.ErrorsEnum;
import lombok.Getter;

@Getter
public class AccountValidationException extends AccountException {

  public AccountValidationException(ErrorsEnum errorsEnum) {
    super(errorsEnum);
  }
}
