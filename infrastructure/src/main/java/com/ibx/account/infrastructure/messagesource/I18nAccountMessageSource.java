package com.ibx.account.infrastructure.messagesource;

import com.ibx.account.domain.messagesource.AccountMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class I18nAccountMessageSource implements AccountMessageSource {

  private final MessageSource messageSource;

  @Override
  public String getMessage(final String code) {
    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
  }
}
