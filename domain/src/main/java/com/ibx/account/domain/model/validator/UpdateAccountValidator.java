package com.ibx.account.domain.model.validator;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountValidationException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class UpdateAccountValidator {

  public void validate(Account account) {
    if (Objects.isNull(account.firstName())) {
      throw new AccountValidationException(ErrorsEnum.UPDATE_ACCOUNT_FIRST_NAME_REQUIRED);
    }

    if (Objects.isNull(account.lastName())) {
      throw new AccountValidationException(ErrorsEnum.UPDATE_ACCOUNT_LAST_NAME_REQUIRED);
    }

    if (Objects.isNull(account.username())) {
      throw new AccountValidationException(ErrorsEnum.UPDATE_ACCOUNT_USERNAME_REQUIRED);
    }

    if (Objects.isNull(account.password())) {
      throw new AccountValidationException(ErrorsEnum.UPDATE_ACCOUNT_PASSWORD_REQUIRED);
    }
  }
}
