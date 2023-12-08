package com.ibx.account.application.controller.mapper;

import com.ibx.account.application.model.AccountDto;
import com.ibx.account.application.model.RegisterRequestDto;
import com.ibx.account.domain.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountRestMapper {

  Account mapRegisterRequestToDomain(RegisterRequestDto registerRequestDto);

  AccountDto mapAccountToInfrastructure(Account account);

}
