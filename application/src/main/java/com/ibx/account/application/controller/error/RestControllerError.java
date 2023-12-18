package com.ibx.account.application.controller.error;

import com.ibx.account.application.model.ErrorDto;
import com.ibx.account.domain.messagesource.AccountMessageSource;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
import com.ibx.account.domain.model.exception.AccountRepositoryException;
import com.ibx.account.domain.model.exception.AccountValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestControllerError {

  private static final String ERROR_DESCRIPTION_FORMAT = "error.%s.description";

  private static final String ERROR_MESSAGE_FORMAT = "error.%s.message";

  private final AccountMessageSource accountMessageSource;

  @ExceptionHandler(AccountValidationException.class)
  public Mono<ResponseEntity<ErrorDto>> accountValidationException(
      final AccountValidationException exception, final ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(AccountAlreadyExistsException.class)
  public Mono<ResponseEntity<ErrorDto>> accountAlreadyExistsException(
      final AccountAlreadyExistsException exception, final ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.CONFLICT));
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public Mono<ResponseEntity<ErrorDto>> accountNotFoundException(
      final AccountNotFoundException exception, final ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.NOT_FOUND));
  }

  @ExceptionHandler(AccountRepositoryException.class)
  public Mono<ResponseEntity<ErrorDto>> accountRepositoryException(
      final AccountRepositoryException exception, final ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.INTERNAL_SERVER_ERROR));
  }

  private ErrorDto buildFromErrorEnum(final ErrorsEnum errorEnum, final String path,
      final Object[] params) {
    final var error = new ErrorDto();
    error.setCode(errorEnum.getCode());
    error.setDescription(getDescription(errorEnum.getCode(), params));
    error.setMessage(getMessage(errorEnum.getCode(), params));
    error.setPath(path);
    return error;
  }

  private String getDescription(final String errorCode, final Object[] params) {
    return getMessageSourceMessage(String.format(ERROR_DESCRIPTION_FORMAT, errorCode), params);
  }

  private String getMessage(final String errorCode, final Object[] params) {
    return getMessageSourceMessage(String.format(ERROR_MESSAGE_FORMAT, errorCode), params);
  }

  private String getMessageSourceMessage(final String code, final Object[] params) {
    return String.format(accountMessageSource.getMessage(code), params);
  }
}
