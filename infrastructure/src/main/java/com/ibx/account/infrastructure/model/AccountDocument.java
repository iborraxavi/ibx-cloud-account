package com.ibx.account.infrastructure.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("account")
public class AccountDocument {

  @Id
  private String id;

  private String username;

  private String password;

  private String firstName;

  private String lastName;

  @Version
  private int version;

}
