package com.ibx.account.application.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ibx.account.application.controller.mapper.AccountRestMapper;
import com.ibx.account.application.model.AccountDto;
import com.ibx.account.application.model.RegisterRequestDto;
import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.usecase.AccountRegister;
import com.ibx.account.domain.usecase.DeleteAccount;
import com.ibx.account.domain.usecase.FindAccountById;
import com.ibx.account.domain.usecase.FindAllAccounts;
import com.ibx.account.domain.usecase.UpdateAccount;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

  @Mock
  private FindAllAccounts findAllAccounts;

  @Mock
  private AccountRegister accountRegister;

  @Mock
  private FindAccountById findAccountById;

  @Mock
  private UpdateAccount updateAccount;

  @Mock
  private DeleteAccount deleteAccount;

  @Mock
  private AccountRestMapper accountRestMapper;

  @InjectMocks
  private AccountController accountController;

  @Test
  @DisplayName("Find all accounts when finn all accounts should return all accounts")
  void findAllAccounts_whenFindAllAccounts_shouldReturnAllAccounts() {
    var serverWebExchange = mock(ServerWebExchange.class);
    var account = new Account(UUID.randomUUID().toString(), "username", "password", "firstName",
        "lastName");
    var accountFlux = Flux.just(account);

    when(findAllAccounts.apply()).thenReturn(accountFlux);

    var result = accountController.findAllAccounts(serverWebExchange);

    StepVerifier.create(result)
        .expectNextCount(1)
        .verifyComplete();

    verify(findAllAccounts, only()).apply();
  }

  @Test
  @DisplayName("Account register when account register should return new account")
  void accountRegister_whenAccountRegister_shouldReturnNewAccount() {
    var serverWebExchange = mock(ServerWebExchange.class);
    var registerRequest = new RegisterRequestDto();
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName",
        "lastName");
    var account = new Account(UUID.randomUUID().toString(), "username", "password", "firstName",
        "lastName");
    var accountDto = new AccountDto();

    when(accountRestMapper.mapRegisterRequestToDomain(registerRequest)).thenReturn(accountRequest);
    when(accountRegister.apply(accountRequest)).thenReturn(Mono.just(account));
    when(accountRestMapper.mapAccountToInfrastructure(account)).thenReturn(accountDto);

    var result = accountController.accountRegister(Mono.just(registerRequest), serverWebExchange);

    StepVerifier.create(result)
        .expectNextMatches(accountDtoResponseEntity ->
            accountDtoResponseEntity.getStatusCode().equals(HttpStatusCode.valueOf(201))
                && Objects.nonNull(accountDtoResponseEntity.getBody())
                && accountDtoResponseEntity.getBody().equals(accountDto))
        .verifyComplete();

    verify(accountRegister, only()).apply(accountRequest);
    verify(accountRestMapper, times(1)).mapRegisterRequestToDomain(registerRequest);
    verify(accountRestMapper, times(1)).mapAccountToInfrastructure(account);
    verifyNoMoreInteractions(accountRestMapper);
  }

  @Test
  @DisplayName("Find account by id when find account by id should return account")
  void findAccountById_whenFindAccountById_shouldReturnAccount() {
    var serverWebExchange = mock(ServerWebExchange.class);
    var accountId = UUID.randomUUID().toString();
    var account = new Account(UUID.randomUUID().toString(), "username", "password", "firstName",
        "lastName");
    var accountDto = new AccountDto();

    when(findAccountById.apply(accountId)).thenReturn(Mono.just(account));
    when(accountRestMapper.mapAccountToInfrastructure(account)).thenReturn(accountDto);

    var result = accountController.findAccountById(accountId, serverWebExchange);

    StepVerifier.create(result)
        .expectNextMatches(accountDtoResponseEntity ->
            accountDtoResponseEntity.getStatusCode().equals(HttpStatusCode.valueOf(200))
                && Objects.nonNull(accountDtoResponseEntity.getBody())
                && accountDtoResponseEntity.getBody().equals(accountDto))
        .verifyComplete();

    verify(findAccountById, only()).apply(accountId);
    verify(accountRestMapper, only()).mapAccountToInfrastructure(account);
  }

  @Test
  @DisplayName("Update account when account register should return new account")
  void updateAccount_whenAccountRegister_shouldReturnNewAccount() {
    var serverWebExchange = mock(ServerWebExchange.class);
    var accountId = UUID.randomUUID().toString();
    var registerRequest = new RegisterRequestDto();
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName",
        "lastName");
    var account = new Account(UUID.randomUUID().toString(), "username", "password", "firstName",
        "lastName");
    var accountDto = new AccountDto();

    when(accountRestMapper.mapRegisterRequestToDomain(registerRequest)).thenReturn(accountRequest);
    when(updateAccount.apply(accountId, accountRequest)).thenReturn(Mono.just(account));
    when(accountRestMapper.mapAccountToInfrastructure(account)).thenReturn(accountDto);

    var result = accountController.updateAccount(accountId, Mono.just(registerRequest),
        serverWebExchange);

    StepVerifier.create(result)
        .expectNextMatches(accountDtoResponseEntity ->
            accountDtoResponseEntity.getStatusCode().equals(HttpStatusCode.valueOf(200))
                && Objects.nonNull(accountDtoResponseEntity.getBody())
                && accountDtoResponseEntity.getBody().equals(accountDto))
        .verifyComplete();

    verify(updateAccount, only()).apply(accountId, accountRequest);
    verify(accountRestMapper, times(1)).mapRegisterRequestToDomain(registerRequest);
    verify(accountRestMapper, times(1)).mapAccountToInfrastructure(account);
    verifyNoMoreInteractions(accountRestMapper);
  }

  @Test
  @DisplayName("Delete account when find account by id should return account")
  void deleteAccount_whenFindAccountById_shouldReturnAccount() {
    var serverWebExchange = mock(ServerWebExchange.class);
    var accountId = UUID.randomUUID().toString();

    when(deleteAccount.apply(accountId)).thenReturn(Mono.empty());

    var result = accountController.deleteAccount(accountId, serverWebExchange);

    StepVerifier.create(result)
        .expectNextMatches(accountDtoResponseEntity ->
            accountDtoResponseEntity.getStatusCode().equals(HttpStatusCode.valueOf(204)))
        .verifyComplete();

    verify(deleteAccount, only()).apply(accountId);
    verifyNoInteractions(accountRestMapper);
  }

}
