package com.ibx.account.domain.usecase.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.exception.AccountValidationException;
import com.ibx.account.domain.model.validator.CreateAccountValidator;
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
class AccountRegisterUseCaseTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private CreateAccountValidator createAccountValidator;

  @InjectMocks
  private AccountRegisterUseCase accountRegisterUseCase;

  @Test
  @DisplayName("Apply when validation error should return expected error")
  void apply_whenValidationError_shouldReturnExpectedError() {
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    doThrow(new AccountValidationException(ErrorsEnum.ACCOUNT_REGISTER_FIRST_NAME_REQUIRED))
        .when(createAccountValidator).validate(accountRequest);

    Mono<Account> result = accountRegisterUseCase.apply(accountRequest);

    StepVerifier.create(result)
        .expectError(AccountValidationException.class)
        .verify();

    verify(createAccountValidator, only()).validate(accountRequest);
    verifyNoInteractions(accountRepository);
  }

  @Test
  @DisplayName("Apply when username already exists should return expected error")
  void apply_whenUsernameAlreadyExists_shouldReturnExpectedError() {
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username1", "password",
        "firstName", "lastName");
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username2", "password",
        "firstName", "lastName");

    doNothing().when(createAccountValidator).validate(accountRequest);
    when(accountRepository.findByUsername(accountRequest.username()))
        .thenReturn(Mono.just(existingAccount));

    Mono<Account> result = accountRegisterUseCase.apply(accountRequest);

    StepVerifier.create(result)
        .expectError(AccountAlreadyExistsException.class)
        .verify();

    verify(createAccountValidator, only()).validate(accountRequest);
    verify(accountRepository, only()).findByUsername(accountRequest.username());
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Apply when username non exists should save account")
  void apply_whenUsernameNonExists_shouldSaveAccount() {
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username1", "password",
        "firstName", "lastName");
    Account savedAccount = new Account(UUID.randomUUID().toString(), "username2", "password",
        "firstName", "lastName");

    doNothing().when(createAccountValidator).validate(accountRequest);
    when(accountRepository.findByUsername(accountRequest.username())).thenReturn(Mono.empty());
    when(accountRepository.save(accountRequest)).thenReturn(Mono.just(savedAccount));

    Mono<Account> result = accountRegisterUseCase.apply(accountRequest);

    StepVerifier.create(result)
        .expectNext(savedAccount)
        .verifyComplete();

    verify(createAccountValidator, only()).validate(accountRequest);
    verify(accountRepository, times(1)).findByUsername(accountRequest.username());
    verify(accountRepository, times(1)).save(accountRequest);
    verifyNoMoreInteractions(accountRepository);
  }
}
