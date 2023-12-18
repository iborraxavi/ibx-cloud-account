package com.ibx.account.infrastructure.repository;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.infrastructure.mapper.AccountDocumentMapper;
import com.ibx.account.infrastructure.mapper.AccountErrorMapper;
import com.ibx.account.infrastructure.model.AccountDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  private final AccountDocumentMapper accountDocumentMapper;

  private final AccountErrorMapper accountErrorMapper;

  @Override
  public Mono<Account> findById(final String id) {
    return mongoTemplate.findById(id, AccountDocument.class)
        .map(accountDocumentMapper::toDomain)
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Mono<Account> findByUsername(final String username) {
    final var query = new Query().addCriteria(Criteria.where("username").is(username));

    return mongoTemplate.findOne(query, AccountDocument.class)
        .map(accountDocumentMapper::toDomain)
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Mono<Account> findByUsernameAndIdNotIs(final String username, final String accountId) {
    final var query = new Query()
        .addCriteria(Criteria.where("username").is(username))
        .addCriteria(Criteria.where("id").ne(accountId));

    return mongoTemplate.findOne(query, AccountDocument.class)
        .map(accountDocumentMapper::toDomain)
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Flux<Account> findAll() {
    return mongoTemplate.findAll(AccountDocument.class)
        .map(accountDocumentMapper::toDomain)
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Mono<Account> save(final Account account) {
    return mongoTemplate.save(accountDocumentMapper.toInfrastructure(account))
        .map(accountDocumentMapper::toDomain)
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Mono<Account> update(final String accountId, final Account account) {
    final var query = new Query()
        .addCriteria(Criteria.where("id").is(accountId));

    final var update = new Update()
        .set("username", account.username())
        .set("password", account.password())
        .set("firstName", account.firstName())
        .set("lastName", account.lastName());

    return mongoTemplate.updateFirst(query, update, AccountDocument.class)
        .flatMap(updateResult -> findById(accountId))
        .onErrorMap(accountErrorMapper::mapMongoError);
  }

  @Override
  public Mono<Void> deleteById(final String id) {
    final var query = new Query()
        .addCriteria(Criteria.where("id").is(id));

    return mongoTemplate.remove(query, AccountDocument.class)
        .onErrorMap(accountErrorMapper::mapMongoError)
        .then();
  }

}
