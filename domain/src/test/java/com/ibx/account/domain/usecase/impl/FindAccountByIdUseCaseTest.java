package com.ibx.account.domain.usecase.impl;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.repository.AccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FindAccountByIdUseCaseTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private FindAccountByIdUseCase findAccountByIdUseCase;

  @Test
  @DisplayName("Apply when account not found should return expected error")
  void apply_whenAccountNotFound_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();

    when(accountRepository.findById(accountId)).thenReturn(Mono.empty());

    Mono<Account> result = findAccountByIdUseCase.apply(accountId);

    StepVerifier.create(result)
        .expectError(AccountNotFoundException.class)
        .verify();

    verify(accountRepository, only()).findById(accountId);
  }

  @Test
  @DisplayName("Apply when account exists should return account")
  void apply_whenAccountExists_shouldReturnAccount() {
    String accountId = UUID.randomUUID().toString();
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));

    Mono<Account> result = findAccountByIdUseCase.apply(accountId);

    StepVerifier.create(result)
        .expectNext(existingAccount)
        .verifyComplete();

    verify(accountRepository, only()).findById(accountId);
  }

}
