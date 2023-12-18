package com.ibx.account.infrastructure.messaging;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibx.account.domain.model.Account;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DeleteAccountKafkaProducerTest {

  private static final String DELETE_ACCOUNT_TOPIC = "delete.account.topic";

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private DeleteAccountKafkaProducer deleteAccountKafkaProducer;

  @BeforeEach
  void beforeEach() {
    ReflectionTestUtils.setField(deleteAccountKafkaProducer, "deleteAccountTopic",
        DELETE_ACCOUNT_TOPIC);
  }

  @Test
  @DisplayName("Send message when object mapper error should do nothing")
  void sendMessage_whenObjectMapperError_shouldDoNothing() throws JsonProcessingException {
    String key = "key";
    Account account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");

    when(objectMapper.writeValueAsString(account)).thenThrow(JsonProcessingException.class);

    deleteAccountKafkaProducer.sendMessage(key, account);

    verify(objectMapper, only()).writeValueAsString(account);
    verifyNoInteractions(kafkaTemplate);
  }

  @Test
  @DisplayName("Send message when send message should send message")
  void sendMessage_whenSendMessage_shouldSendMessage() throws JsonProcessingException {
    String key = "key";
    Account account = new Account(UUID.randomUUID().toString(), "username", "password",
        "firstName", "lastName");
    String accountJson = new ObjectMapper().writeValueAsString(account);

    when(objectMapper.writeValueAsString(account)).thenReturn(accountJson);
    when(kafkaTemplate.send(DELETE_ACCOUNT_TOPIC, key, accountJson)).thenReturn(null);

    deleteAccountKafkaProducer.sendMessage(key, account);

    verify(objectMapper, only()).writeValueAsString(account);
    verify(kafkaTemplate, only()).send(DELETE_ACCOUNT_TOPIC, key, accountJson);
  }

}
