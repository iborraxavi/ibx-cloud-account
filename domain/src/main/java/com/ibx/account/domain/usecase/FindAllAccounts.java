package com.ibx.account.domain.usecase;

import com.ibx.account.domain.model.Account;
import reactor.core.publisher.Flux;

public interface FindAllAccounts {

  Flux<Account> apply();

}
