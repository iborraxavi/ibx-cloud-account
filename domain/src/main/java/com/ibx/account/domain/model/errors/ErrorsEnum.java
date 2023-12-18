package com.ibx.account.domain.model.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorsEnum {
  ACCOUNT_REGISTER_FIRST_NAME_REQUIRED("ACCOUNT_0001",
      "First name is required in account registration"),
  ACCOUNT_REGISTER_LAST_NAME_REQUIRED("ACCOUNT_0002",
      "Last name is required in account registration"),
  ACCOUNT_REGISTER_USERNAME_REQUIRED("ACCOUNT_0003",
      "Username is required in account registration"),
  ACCOUNT_REGISTER_PASSWORD_REQUIRED("ACCOUNT_0004",
      "Password is required in account registration"),
  ACCOUNT_REGISTER_USERNAME_ALREADY_EXISTS("ACCOUNT_0005",
      "Username already exists in account registration"),

  ACCOUNT_NOT_FOUND("ACCOUNT_0006", "Account not found"),

  UPDATE_ACCOUNT_FIRST_NAME_REQUIRED("ACCOUNT_0007",
      "First name is required in account update"),
  UPDATE_ACCOUNT_LAST_NAME_REQUIRED("ACCOUNT_0008",
      "Last name is required in account update"),
  UPDATE_ACCOUNT_USERNAME_REQUIRED("ACCOUNT_0009",
      "Username is required in account update"),
  UPDATE_ACCOUNT_PASSWORD_REQUIRED("ACCOUNT_0010",
      "Password is required in account update"),
  UPDATE_ACCOUNT_USERNAME_ALREADY_EXISTS("ACCOUNT_0011",
      "Username already exists in account update"),

  INTERNAL_SERVER_ERROR("ACCOUNT_0012", "Unexpected error");

  private final String code;

  private final String message;

}