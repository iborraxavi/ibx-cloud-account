package com.ibx.account.domain.messaging;

import com.ibx.account.domain.model.Account;

public interface DeleteAccountProducer {

  void sendMessage(String key, Account account);

}
