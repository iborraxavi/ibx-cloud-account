package com.ibx.account.domain.usecase.impl;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.domain.usecase.FindAllAccounts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FindAllAccountsUseCase implements FindAllAccounts {

  private final AccountRepository accountRepository;

  @Override
  public Flux<Account> apply() {
    return accountRepository.findAll();
  }

}
