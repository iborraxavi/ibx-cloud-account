package com.ibx.account.domain.usecase.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ibx.account.domain.messaging.DeleteAccountProducer;
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
class DeleteAccountUseCaseTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private DeleteAccountProducer deleteAccountProducer;

  @InjectMocks
  private DeleteAccountUseCase deleteAccountUseCase;

  @Test
  @DisplayName("Apply when account not found should return expected error")
  void apply_whenAccountNotFound_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();

    when(accountRepository.findById(accountId)).thenReturn(Mono.empty());

    Mono<Void> result = deleteAccountUseCase.apply(accountId);

    StepVerifier.create(result)
        .expectError(AccountNotFoundException.class)
        .verify();

    verify(accountRepository, only()).findById(accountId);
    verifyNoInteractions(deleteAccountProducer);
  }

  @Test
  @DisplayName("Apply when delete account error should return expected error")
  void apply_whenDeleteAccountError_shouldReturnExpectedError() {
    String accountId = UUID.randomUUID().toString();
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));
    when(accountRepository.deleteById(existingAccount.id())).thenReturn(
        Mono.error(new IllegalArgumentException()));

    Mono<Void> result = deleteAccountUseCase.apply(accountId);

    StepVerifier.create(result)
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountRepository, times(1)).deleteById(existingAccount.id());
    verifyNoMoreInteractions(accountRepository);
    verifyNoInteractions(deleteAccountProducer);
  }

  @Test
  @DisplayName("Apply when delete success should return expected response")
  void apply_whenDeleteSuccess_shouldReturnExpectedResponse() {
    String accountId = UUID.randomUUID().toString();
    Account existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(accountRepository.findById(accountId)).thenReturn(Mono.just(existingAccount));
    when(accountRepository.deleteById(existingAccount.id())).thenReturn(Mono.empty());
    doNothing().when(deleteAccountProducer).sendMessage(existingAccount.id(), existingAccount);

    Mono<Void> result = deleteAccountUseCase.apply(accountId);

    StepVerifier.create(result)
        .verifyComplete();

    verify(accountRepository, times(1)).findById(accountId);
    verify(accountRepository, times(1)).deleteById(existingAccount.id());
    verifyNoMoreInteractions(accountRepository);
    verify(deleteAccountProducer, only()).sendMessage(existingAccount.id(), existingAccount);
  }

}
