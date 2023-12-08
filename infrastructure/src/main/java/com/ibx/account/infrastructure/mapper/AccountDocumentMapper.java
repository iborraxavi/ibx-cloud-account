package com.ibx.account.infrastructure.mapper;

import com.ibx.account.domain.model.Account;
import com.ibx.account.infrastructure.model.AccountDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountDocumentMapper {

  AccountDocument toInfrastructure(Account account);

  Account toDomain(AccountDocument accountDocument);

}
