package com.ibx.account.domain.usecase.impl;

import com.ibx.account.domain.messaging.DeleteAccountProducer;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.domain.usecase.DeleteAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteAccountUseCase implements DeleteAccount {

  private final AccountRepository accountRepository;

  private final DeleteAccountProducer deleteAccountProducer;

  @Override
  public Mono<Void> apply(final String accountId) {
    return accountRepository.findById(accountId)
        .switchIfEmpty(
            Mono.error(new AccountNotFoundException(ErrorsEnum.ACCOUNT_NOT_FOUND, accountId)))
        .flatMap(account -> accountRepository.deleteById(account.id())
            .doOnSuccess(unused -> deleteAccountProducer.sendMessage(account.id(), account)));
  }

}
