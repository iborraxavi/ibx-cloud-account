package com.ibx.account.application.controller;

import com.ibx.account.application.api.AccountApi;
import com.ibx.account.application.controller.mapper.AccountRestMapper;
import com.ibx.account.application.model.AccountDto;
import com.ibx.account.application.model.RegisterRequestDto;
import com.ibx.account.domain.usecase.AccountRegister;
import com.ibx.account.domain.usecase.DeleteAccount;
import com.ibx.account.domain.usecase.FindAccountById;
import com.ibx.account.domain.usecase.FindAllAccounts;
import com.ibx.account.domain.usecase.UpdateAccount;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

  private final FindAllAccounts findAllAccounts;

  private final AccountRegister accountRegister;

  private final FindAccountById findAccountById;

  private final UpdateAccount updateAccount;

  private final DeleteAccount deleteAccount;

  private final AccountRestMapper accountRestMapper;

  @Override
  public Mono<ResponseEntity<Flux<AccountDto>>> findAllAccounts(ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(findAllAccounts.apply()
        .map(accountRestMapper::mapAccountToInfrastructure), HttpStatus.OK));
  }

  @Override
  public Mono<ResponseEntity<AccountDto>> accountRegister(
      @Valid @RequestBody Mono<RegisterRequestDto> registerRequestDto, ServerWebExchange exchange) {
    return registerRequestDto
        .map(accountRestMapper::mapRegisterRequestToDomain)
        .flatMap(accountRegister::apply)
        .map(account -> new ResponseEntity<>(accountRestMapper.mapAccountToInfrastructure(account),
            HttpStatus.CREATED));
  }

  @Override
  public Mono<ResponseEntity<AccountDto>> findAccountById(String accountId,
      ServerWebExchange exchange) {
    return findAccountById.apply(accountId)
        .map(account -> new ResponseEntity<>(accountRestMapper.mapAccountToInfrastructure(account),
            HttpStatus.OK));
  }

  @Override
  public Mono<ResponseEntity<AccountDto>> updateAccount(String accountId,
      Mono<RegisterRequestDto> registerRequestDto, ServerWebExchange exchange) {
    return registerRequestDto
        .map(accountRestMapper::mapRegisterRequestToDomain)
        .flatMap(registerRequest -> updateAccount.apply(accountId, registerRequest))
        .map(account -> new ResponseEntity<>(accountRestMapper.mapAccountToInfrastructure(account),
            HttpStatus.OK));
  }

  @Override
  public Mono<ResponseEntity<Void>> deleteAccount(String accountId,
      ServerWebExchange exchange) {
    return deleteAccount.apply(accountId)
        .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }
}