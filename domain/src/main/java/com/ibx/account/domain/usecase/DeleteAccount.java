package com.ibx.account.domain.usecase;

import reactor.core.publisher.Mono;

public interface DeleteAccount {

  Mono<Void> apply(String accountId);

}
