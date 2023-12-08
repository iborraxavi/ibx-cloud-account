package com.ibx.account.domain.usecase;

import com.ibx.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface UpdateAccount {

  Mono<Account> apply(String accountId, Account accountRequest);

}
