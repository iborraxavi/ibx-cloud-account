package com.ibx.account.infrastructure.repository;

import com.ibx.account.domain.model.Account;
import com.ibx.account.domain.repository.AccountRepository;
import com.ibx.account.infrastructure.mapper.AccountDocumentMapper;
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

  @Override
  public Mono<Account> findById(String id) {
    return mongoTemplate.findById(id, Account.class);
  }

  @Override
  public Mono<Account> findByUsername(String username) {
    Query query = new Query().addCriteria(Criteria.where("username").is(username));

    return mongoTemplate.findOne(query, Account.class);
  }

  @Override
  public Mono<Account> findByUsernameAndIdNotIs(String username, String accountId) {
    Query query = new Query()
        .addCriteria(Criteria.where("username").is(username))
        .addCriteria(Criteria.where("id").ne(accountId));

    return mongoTemplate.findOne(query, Account.class);
  }

  @Override
  public Flux<Account> findAll() {
    return mongoTemplate.findAll(Account.class);
  }

  @Override
  public Mono<Account> save(Account account) {
    return mongoTemplate.save(accountDocumentMapper.toInfrastructure(account))
        .map(accountDocumentMapper::toDomain);
  }

  @Override
  public Mono<Account> update(String accountId, Account account) {
    Query query = new Query()
        .addCriteria(Criteria.where("id").is(accountId));

    Update update = new Update()
        .set("username", account.username())
        .set("password", account.password())
        .set("firstName", account.firstName())
        .set("lastName", account.lastName());

    return mongoTemplate.updateFirst(query, update, Account.class)
        .flatMap(updateResult -> findById(accountId));
  }

  @Override
  public Mono<Void> deleteById(String id) {
    Query query = new Query()
        .addCriteria(Criteria.where("id").is(id));

    return mongoTemplate.remove(query, Account.class)
        .then();
  }
}
