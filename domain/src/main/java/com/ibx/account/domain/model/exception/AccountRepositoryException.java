package com.ibx.account.domain.model.exception;

import com.ibx.account.domain.model.errors.ErrorsEnum;
import lombok.Getter;

@Getter
public class AccountRepositoryException extends AccountException {

  public AccountRepositoryException(ErrorsEnum errorsEnum) {
    super(errorsEnum);
  }

}
