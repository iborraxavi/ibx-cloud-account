package com.ibx.account.domain.usecase;

import com.ibx.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface FindAccountById {

  Mono<Account> apply(String id);

}
