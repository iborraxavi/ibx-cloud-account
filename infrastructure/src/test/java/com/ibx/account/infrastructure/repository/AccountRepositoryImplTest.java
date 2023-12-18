package com.ibx.account.infrastructure.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountRepositoryException;
import com.ibx.account.infrastructure.mapper.AccountDocumentMapper;
import com.ibx.account.infrastructure.mapper.AccountErrorMapper;
import com.ibx.account.infrastructure.model.AccountDocument;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.UUID;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

  @Mock
  private ReactiveMongoTemplate mongoTemplate;

  @Mock
  private AccountDocumentMapper accountDocumentMapper;

  @Mock
  private AccountErrorMapper accountErrorMapper;

  @InjectMocks
  private AccountRepositoryImpl accountRepositoryImpl;

  @Test
  @DisplayName("Find by id when mongo error then return expected error")
  void findById_whenMongoError_thenReturnExpectedError() {
    var accountId = UUID.randomUUID().toString();
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.findById(accountId, AccountDocument.class))
        .thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.findById(accountId);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, only()).findById(accountId, AccountDocument.class);
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Find by id when success then return expected account")
  void findById_whenSuccess_thenReturnExpectedAccount() {
    var accountId = UUID.randomUUID().toString();
    var accountDocument = new AccountDocument();
    var account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(mongoTemplate.findById(accountId, AccountDocument.class))
        .thenReturn(Mono.just(accountDocument));
    when(accountDocumentMapper.toDomain(accountDocument)).thenReturn(account);

    var result = accountRepositoryImpl.findById(accountId);

    StepVerifier.create(result)
        .expectNext(account)
        .verifyComplete();

    verify(mongoTemplate, only()).findById(accountId, AccountDocument.class);
    verify(accountDocumentMapper, only()).toDomain(accountDocument);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Find by username when mongo error then return expected error")
  void findByUsername_whenMongoError_thenReturnExpectedError() {
    var accountId = UUID.randomUUID().toString();
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.findOne(any(Query.class), eq(AccountDocument.class)))
        .thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.findByUsername(accountId);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, only()).findOne(argThat(getQueryArgumentMatcher(List.of("username"))),
        eq(AccountDocument.class));
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Find by username when success then return expected account")
  void findByUsername_whenSuccess_thenReturnExpectedAccount() {
    var username = "username";
    var accountDocument = new AccountDocument();
    var account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(mongoTemplate.findOne(any(), eq(AccountDocument.class)))
        .thenReturn(Mono.just(accountDocument));
    when(accountDocumentMapper.toDomain(accountDocument)).thenReturn(account);

    var result = accountRepositoryImpl.findByUsername(username);

    StepVerifier.create(result)
        .expectNext(account)
        .verifyComplete();

    verify(mongoTemplate, only()).findOne(argThat(getQueryArgumentMatcher(List.of("username"))),
        eq(AccountDocument.class));
    verify(accountDocumentMapper, only()).toDomain(accountDocument);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Find by username and id not is when mongo error then return expected error")
  void findByUsernameAndIdNotIs_whenMongoError_thenReturnExpectedError() {
    var username = "username";
    var accountId = UUID.randomUUID().toString();
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.findOne(any(Query.class), eq(AccountDocument.class)))
        .thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.findByUsernameAndIdNotIs(username, accountId);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, only()).findOne(
        argThat(getQueryArgumentMatcher(List.of("username", "id"))),
        eq(AccountDocument.class));
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Find by username and id not is when success then return expected account")
  void findByUsernameAndIdNotIs_whenSuccess_thenReturnExpectedAccount() {
    var username = "username";
    var accountId = UUID.randomUUID().toString();
    var accountDocument = new AccountDocument();
    var account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(mongoTemplate.findOne(any(), eq(AccountDocument.class)))
        .thenReturn(Mono.just(accountDocument));
    when(accountDocumentMapper.toDomain(accountDocument)).thenReturn(account);

    var result = accountRepositoryImpl.findByUsernameAndIdNotIs(username, accountId);

    StepVerifier.create(result)
        .expectNext(account)
        .verifyComplete();

    verify(mongoTemplate, only()).findOne(
        argThat(getQueryArgumentMatcher(List.of("username", "id"))),
        eq(AccountDocument.class));
    verify(accountDocumentMapper, only()).toDomain(accountDocument);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Find all when mongo error then return expected error")
  void findAll_whenMongoError_thenReturnExpectedError() {
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.findAll(AccountDocument.class))
        .thenReturn(Flux.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.findAll();

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, only()).findAll(AccountDocument.class);
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Find all when success then return expected accounts")
  void findAll_whenSuccess_thenReturnExpectedAccounts() {
    var accountDocument = new AccountDocument();
    var account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(mongoTemplate.findAll(AccountDocument.class))
        .thenReturn(Flux.just(accountDocument));
    when(accountDocumentMapper.toDomain(accountDocument)).thenReturn(account);

    var result = accountRepositoryImpl.findAll();

    StepVerifier.create(result)
        .expectNextCount(1)
        .verifyComplete();

    verify(mongoTemplate, only()).findAll(AccountDocument.class);
    verify(accountDocumentMapper, only()).toDomain(accountDocument);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Save when mongo error then return expected error")
  void save_whenMongoError_thenReturnExpectedError() {
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    var accountDocumentRequest = new AccountDocument();
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(accountDocumentMapper.toInfrastructure(accountRequest)).thenReturn(accountDocumentRequest);
    when(mongoTemplate.save(accountDocumentRequest)).thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.save(accountRequest);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(accountDocumentMapper, only()).toInfrastructure(accountRequest);
    verify(mongoTemplate, only()).save(accountDocumentRequest);
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
  }

  @Test
  @DisplayName("Save when success then return expected account")
  void save_whenSuccess_thenReturnExpectedAccount() {
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    var accountDocumentRequest = new AccountDocument();
    var accountDocument = new AccountDocument();
    var account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(accountDocumentMapper.toInfrastructure(accountRequest)).thenReturn(accountDocumentRequest);
    when(mongoTemplate.save(accountDocumentRequest)).thenReturn(Mono.just(accountDocument));
    when(accountDocumentMapper.toDomain(accountDocument)).thenReturn(account);

    var result = accountRepositoryImpl.save(accountRequest);

    StepVerifier.create(result)
        .expectNext(account)
        .verifyComplete();

    verify(accountDocumentMapper, times(1)).toInfrastructure(accountRequest);
    verify(accountDocumentMapper, times(1)).toDomain(accountDocument);
    verifyNoMoreInteractions(accountDocumentMapper);
    verify(mongoTemplate, only()).save(accountDocumentRequest);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Update when mongo error then return expected error")
  void update_whenMongoError_thenReturnExpectedError() {
    var accountRequestId = UUID.randomUUID().toString();
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class),
        eq(AccountDocument.class)))
        .thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.update(accountRequestId, accountRequest);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, times(1))
        .updateFirst(argThat(getQueryArgumentMatcher(List.of("id"))),
            argThat(getUpdateDefinitionArgumentMatcher(
                List.of("username", "password", "firstName", "lastName"))),
            eq(AccountDocument.class));
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Update when success then return expected account")
  void update_whenSuccess_thenReturnExpectedAccount() {
    var accountRequestId = UUID.randomUUID().toString();
    var accountRequest = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    var existingAccountDocument = new AccountDocument();
    var existingAccount = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class),
        eq(AccountDocument.class)))
        .thenReturn(Mono.just(UpdateResult.acknowledged(1, 1L, new BsonTimestamp())));
    when(mongoTemplate.findById(accountRequestId, AccountDocument.class))
        .thenReturn(Mono.just(existingAccountDocument));
    when(accountDocumentMapper.toDomain(existingAccountDocument)).thenReturn(existingAccount);

    var result = accountRepositoryImpl.update(accountRequestId, accountRequest);

    StepVerifier.create(result)
        .expectNext(existingAccount)
        .verifyComplete();

    verify(mongoTemplate, times(1))
        .updateFirst(argThat(getQueryArgumentMatcher(List.of("id"))),
            argThat(getUpdateDefinitionArgumentMatcher(
                List.of("username", "password", "firstName", "lastName"))),
            eq(AccountDocument.class));
    verify(mongoTemplate, times(1)).findById(accountRequestId, AccountDocument.class);
    verifyNoMoreInteractions(mongoTemplate);
    verify(accountDocumentMapper, only()).toDomain(existingAccountDocument);
    verifyNoInteractions(accountErrorMapper);
  }

  @Test
  @DisplayName("Delete by id when mongo error then return expected error")
  void deleteById_whenMongoError_thenReturnExpectedError() {
    var accountId = UUID.randomUUID().toString();
    var mongoException = new MongoException("");
    var accountRepositoryException = new AccountRepositoryException(
        ErrorsEnum.INTERNAL_SERVER_ERROR);

    when(mongoTemplate.remove(any(Query.class), eq(AccountDocument.class)))
        .thenReturn(Mono.error(mongoException));
    when(accountErrorMapper.mapMongoError(mongoException)).thenReturn(accountRepositoryException);

    var result = accountRepositoryImpl.deleteById(accountId);

    StepVerifier.create(result)
        .expectError(AccountRepositoryException.class)
        .verify();

    verify(mongoTemplate, only()).remove(argThat(getQueryArgumentMatcher(List.of("id"))),
        eq(AccountDocument.class));
    verify(accountErrorMapper, only()).mapMongoError(mongoException);
    verifyNoInteractions(accountDocumentMapper);
  }

  @Test
  @DisplayName("Delete by id when success then return expected account")
  void deleteById_whenSuccess_thenReturnExpectedAccount() {
    var accountId = UUID.randomUUID().toString();

    when(mongoTemplate.remove(any(Query.class), eq(AccountDocument.class)))
        .thenReturn(Mono.empty());

    var result = accountRepositoryImpl.deleteById(accountId);

    StepVerifier.create(result)
        .verifyComplete();

    verify(mongoTemplate, only()).remove(argThat(getQueryArgumentMatcher(List.of("id"))),
        eq(AccountDocument.class));
    verifyNoInteractions(accountDocumentMapper);
    verifyNoInteractions(accountErrorMapper);
  }

  private ArgumentMatcher<Query> getQueryArgumentMatcher(final List<String> keys) {
    return query -> keys.stream()
        .allMatch(key -> query.getQueryObject().containsKey(key));
  }

  private ArgumentMatcher<UpdateDefinition> getUpdateDefinitionArgumentMatcher(
      final List<String> keys) {
    return updateDefinition -> {
      Document document = (Document) updateDefinition.getUpdateObject().get("$set");

      return keys.stream()
          .allMatch(document::containsKey);
    };
  }

}
