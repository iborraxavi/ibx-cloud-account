package com.ibx.account.domain.usecase;

import com.ibx.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountRegister {

  Mono<Account> apply(Account account);

}
