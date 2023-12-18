package com.ibx.account.domain.usecase.impl;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.model.validator.UpdateAccountValidator;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.domain.usecase.UpdateAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateAccountUseCase implements UpdateAccount {

  private final AccountRepository accountRepository;

  private final UpdateAccountValidator validator;

  @Override
  public Mono<Account> apply(final String accountId, final Account accountRequest) {
    return Mono.just(accountRequest)
        .doOnNext(validator::validate)
        .flatMap(account -> validateExistingAccount(accountId, account))
        .flatMap(existingAccount -> updateAccount(accountId, accountRequest, existingAccount));
  }

  private Mono<Account> validateExistingAccount(final String accountId, final Account account) {
    return accountRepository.findById(accountId)
        .switchIfEmpty(Mono.error(
            new AccountNotFoundException(ErrorsEnum.ACCOUNT_NOT_FOUND, accountId)))
        .thenReturn(account);
  }

  private Mono<Account> updateAccount(final String accountId, final Account accountRequest,
      final Account existingAccount) {
    return validateExistingUsername(accountId, accountRequest, existingAccount)
        .switchIfEmpty(Mono.defer(() -> accountRepository.update(accountId, accountRequest)));
  }

  private Mono<Account> validateExistingUsername(final String accountId,
      final Account accountRequest, final Account existingAccount) {
    return accountRepository.findByUsernameAndIdNotIs(accountRequest.username(),
            accountId)
        .flatMap(existingAccountWithExistingUsername ->
            Mono.error(new AccountAlreadyExistsException(
                    ErrorsEnum.UPDATE_ACCOUNT_USERNAME_ALREADY_EXISTS,
                    existingAccountWithExistingUsername.username()))
                .thenReturn(existingAccount));
  }

}
