package com.ibx.account.domain.model.exception;

import com.ibx.account.domain.model.errors.ErrorsEnum;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends AccountException {

  public AccountNotFoundException(ErrorsEnum errorsEnum, String... params) {
    super(errorsEnum, params);
  }

}
