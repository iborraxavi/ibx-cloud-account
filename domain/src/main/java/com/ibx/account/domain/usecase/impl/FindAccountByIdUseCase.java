package com.ibx.account.domain.usecase.impl;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.domain.usecase.FindAccountById;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindAccountByIdUseCase implements FindAccountById {

  private final AccountRepository accountRepository;

  @Override
  public Mono<Account> apply(String id) {
    return accountRepository.findById(id)
        .switchIfEmpty(Mono.error(new AccountNotFoundException(ErrorsEnum.ACCOUNT_NOT_FOUND, id)));
  }

}
