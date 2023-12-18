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
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.model.exception.AccountValidationException;
import com.ibx.account.domain.model.validator.UpdateAccountValidator;
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
class UpdateAccountUseCaseTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UpdateAccountValidator validator;

  @InjectMocks
  private UpdateAccountUseCase updateAccountUseCase;

  @Test
  @DisplayName("Apply when validation error should return expected error")
  void apply_whenValidationError_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    doThrow(AccountValidationException.class).when(validator).validate(accountRequest);

    Mono<Account> result = updateAccountUseCase.apply(accountId, accountRequest);

    StepVerifier.create(result)
        .expectError(AccountValidationException.class)
        .verify();

    verify(validator, only()).validate(accountRequest);
    verifyNoInteractions(accountRepository);
  }

  @Test
  @DisplayName("Apply when account not found should return expected error")
  void apply_whenAccountNotFound_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    doNothing().when(validator).validate(accountRequest);
    when(accountRepository.findById(accountId)).thenReturn(Mono.empty());

    Mono<Account> result = updateAccountUseCase.apply(accountId, accountRequest);

    StepVerifier.create(result)
        .expectError(AccountNotFoundException.class)
        .verify();

    verify(validator, only()).validate(accountRequest);
    verify(accountRepository, only()).findById(accountId);
  }

  @Test
  @DisplayName("Apply when already exists username should return expected error")
  void apply_whenAlreadyExistsUsername_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    Account existingUsernameAccount = new Account(UUID.randomUUID().toString(), "username",
        "password",
        "firstName", "lastName");

    doNothing().when(validator).validate(accountRequest);
    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));
    when(accountRepository.findByUsernameAndIdNotIs(accountRequest.username(), accountId))
        .thenReturn(Mono.just(existingUsernameAccount));

    Mono<Account> result = updateAccountUseCase.apply(accountId, accountRequest);

    StepVerifier.create(result)
        .expectError(AccountAlreadyExistsException.class)
        .verify();

    verify(validator, only()).validate(accountRequest);
    verify(accountRepository, times(1)).findById(accountId);
    verify(accountRepository, times(1)).findByUsernameAndIdNotIs(accountRequest.username(),
        accountId);
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Apply when update error should return expected error")
  void apply_whenUpdateError_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    doNothing().when(validator).validate(accountRequest);
    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));
    when(accountRepository.findByUsernameAndIdNotIs(accountRequest.username(), accountId))
        .thenReturn(Mono.empty());
    when(accountRepository.update(accountId, accountRequest))
        .thenReturn(Mono.error(new IllegalArgumentException()));

    Mono<Account> result = updateAccountUseCase.apply(accountId, accountRequest);

    StepVerifier.create(result)
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(validator, only()).validate(accountRequest);
    verify(accountRepository, times(1)).findById(accountId);
    verify(accountRepository, times(1)).findByUsernameAndIdNotIs(accountRequest.username(),
        accountId);
    verify(accountRepository, times(1)).update(accountId, accountRequest);
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Apply when update success should return updated account")
  void apply_whenUpdateSuccess_shouldReturnUpdatedAccount() {
    String accountId = UUID.randomUUID().toString();
    Account accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    Account updatedAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    doNothing().when(validator).validate(accountRequest);
    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));
    when(accountRepository.findByUsernameAndIdNotIs(accountRequest.username(), accountId))
        .thenReturn(Mono.empty());
    when(accountRepository.update(accountId, accountRequest))
        .thenReturn(Mono.just(updatedAccount));

    Mono<Account> result = updateAccountUseCase.apply(accountId, accountRequest);

    StepVerifier.create(result)
        .expectNext(updatedAccount)
        .verifyComplete();

    verify(validator, only()).validate(accountRequest);
    verify(accountRepository, times(1)).findById(accountId);
    verify(accountRepository, times(1)).findByUsernameAndIdNotIs(accountRequest.username(),
        accountId);
    verify(accountRepository, times(1)).update(accountId, accountRequest);
    verifyNoMoreInteractions(accountRepository);
  }

}
