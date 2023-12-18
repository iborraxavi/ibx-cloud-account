package com.ibx.account.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibx.account.domain.messaging.DeleteAccountProducer;
import com.ibx.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteAccountKafkaProducer implements DeleteAccountProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper;

  @Value("${app.kafka.topic.delete-account}")
  private String deleteAccountTopic;

  @Override
  public void sendMessage(final String key, final @Payload Account account) {
    try {
      kafkaTemplate.send(deleteAccountTopic, key, objectMapper.writeValueAsString(account));
    } catch (JsonProcessingException e) {
      log.info("Error in account delete message format for account: {}", key);
    }
  }
}
