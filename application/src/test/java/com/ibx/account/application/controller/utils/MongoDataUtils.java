package com.ibx.account.application.controller.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibx.account.infrastructure.model.AccountDocument;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class MongoDataUtils {

  private static final String ACCOUNT_DATA_PATH = "src/test/resources/data/accounts.json";

  private final ReactiveMongoTemplate mongoTemplate;

  public void initDb() throws IOException {
    deleteAllAccounts();

    saveAccounts();
  }

  private void deleteAllAccounts() {
    mongoTemplate.remove(new Query(), AccountDocument.class)
        .subscribe(deleteResult -> log.info("Remove all accounts from test db: {}",
            deleteResult.getDeletedCount()));
  }

  private void saveAccounts() throws IOException {
    final AccountDocument[] accountDocuments = new ObjectMapper()
        .readValue(new File(ACCOUNT_DATA_PATH), AccountDocument[].class);

    mongoTemplate.insertAll(Arrays.asList(accountDocuments))
        .subscribe(
            accountDocument -> log.info("Save account in test db: {}", accountDocument.getId()));
  }

}