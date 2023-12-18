package com.ibx.account.domain.usecase.impl;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.repository.AccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FindAllAccountsUseCaseTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private FindAllAccountsUseCase findAllAccountsUseCase;

  @Test
  @DisplayName("Apply when find all should return all accounts")
  void apply_whenFindAll_shouldReturnAllAccounts() {
    Flux<Account> expectedResult = Flux.just(
        new Account(UUID.randomUUID().toString(), "username", "password", "firstName", "lastName"));

    when(accountRepository.findAll()).thenReturn(expectedResult);

    Flux<Account> result = findAllAccountsUseCase.apply();

    StepVerifier.create(result)
        .expectNextCount(1)
        .verifyComplete();

    verify(accountRepository, only()).findAll();
  }
}
