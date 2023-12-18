package com.ibx.account.infrastructure.messagesource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class I18nAccountMessageSourceTest {

  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private I18nAccountMessageSource i18nAccountMessageSource;

  @Test
  @DisplayName("Get message when message exists should return message")
  void getMessage_whenMessageExists_shouldReturnMessage() {
    String code = "error.ERROR01.message";
    String message = "Error message";

    when(messageSource.getMessage(eq(code), isNull(), any(Locale.class))).thenReturn(message);

    var result = i18nAccountMessageSource.getMessage(code);

    assertNotNull(result);
    assertEquals(message, result);

    verify(messageSource, only()).getMessage(eq(code), isNull(), any(Locale.class));
  }

}