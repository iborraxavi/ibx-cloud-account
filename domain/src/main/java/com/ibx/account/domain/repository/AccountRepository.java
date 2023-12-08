package com.ibx.account.domain.repository;

import com.ibx.account.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {

  Mono<Account> findById(String id);

  Mono<Account> findByUsername(String username);

  Mono<Account> findByUsernameAndIdNotIs(String username, String accountId);

  Flux<Account> findAll();

  Mono<Account> save(Account registerRequest);

  Mono<Account> update(String accountId, Account registerRequest);

  Mono<Void> deleteById(String id);

}
