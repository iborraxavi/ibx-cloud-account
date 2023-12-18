package com.ibx.account.domain.usecase.impl;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.validator.CreateAccountValidator;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.domain.usecase.AccountRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountRegisterUseCase implements AccountRegister {

  private final AccountRepository accountRepository;

  private final CreateAccountValidator validator;

  @Override
  public Mono<Account> apply(final Account account) {
    return Mono.just(account)
        .doOnNext(validator::validate)
        .flatMap(this::validateExistingUsername);
  }

  private Mono<Account> validateExistingUsername(final Account validatedAccount) {
    return accountRepository.findByUsername(validatedAccount.username())
        .flatMap(existingAccount -> Mono.error(new AccountAlreadyExistsException(
                ErrorsEnum.ACCOUNT_REGISTER_USERNAME_ALREADY_EXISTS,
                validatedAccount.username()))
            .thenReturn(existingAccount))
        .switchIfEmpty(Mono.defer(() -> accountRepository.save(validatedAccount)));
  }

}
