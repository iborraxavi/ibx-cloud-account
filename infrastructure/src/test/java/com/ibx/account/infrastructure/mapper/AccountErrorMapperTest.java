package com.ibx.account.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ibx.account.domain.model.exception.AccountRepositoryException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountErrorMapperTest {

  @InjectMocks
  private AccountErrorMapper accountErrorMapper;

  @Test
  @DisplayName("Map Mongo error when map exception then return expected exception")
  void mapMongoError_whenMapException_thenReturnExpectedException() {
    var result = accountErrorMapper.mapMongoError(new MongoException(""));

    assertNotNull(result);
    assertTrue(result instanceof AccountRepositoryException);
  }
}
