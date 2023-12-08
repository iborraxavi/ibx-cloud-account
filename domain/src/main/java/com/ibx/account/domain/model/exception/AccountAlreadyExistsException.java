package com.ibx.account.domain.model.exception;

import com.ibx.account.domain.model.errors.ErrorsEnum;
import lombok.Getter;

@Getter
public class AccountAlreadyExistsException extends AccountException {

  public AccountAlreadyExistsException(ErrorsEnum errorsEnum, String... params) {
    super(errorsEnum, params);
  }

}
