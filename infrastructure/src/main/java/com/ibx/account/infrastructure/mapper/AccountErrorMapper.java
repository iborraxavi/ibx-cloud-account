package com.ibx.account.infrastructure.mapper;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountRepositoryException;
import com.ibx.account.infrastructure.model.AccountDocument;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountErrorMapper {

  public Throwable mapMongoError(final Throwable throwable) {
    log.error(throwable.getMessage(), throwable);

    return new AccountRepositoryException(ErrorsEnum.INTERNAL_SERVER_ERROR);
  }

}
