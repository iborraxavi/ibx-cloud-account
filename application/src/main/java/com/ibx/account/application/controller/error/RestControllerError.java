package com.ibx.account.application.controller.error;

import com.ibx.account.application.model.ErrorDto;
import com.ibx.account.domain.messagesource.AccountMessageSource;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import com.ibx.account.domain.model.exception.AccountAlreadyExistsException;
import com.ibx.account.domain.model.exception.AccountNotFoundException;
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
      AccountValidationException exception, ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(AccountAlreadyExistsException.class)
  public Mono<ResponseEntity<ErrorDto>> accountAlreadyExistsException(
      AccountAlreadyExistsException exception, ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.CONFLICT));
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public Mono<ResponseEntity<ErrorDto>> accountNotFoundException(
      AccountNotFoundException exception, ServerWebExchange exchange) {
    return Mono.just(new ResponseEntity<>(
        buildFromErrorEnum(exception.getErrorsEnum(), exchange.getRequest().getPath().value(),
            exception.getParams()),
        HttpStatus.NOT_FOUND));
  }

  private ErrorDto buildFromErrorEnum(ErrorsEnum errorEnum, String path, Object[] params) {
    final ErrorDto error = new ErrorDto();
    error.setCode(errorEnum.getCode());
    error.setDescription(getDescription(errorEnum.getCode(), params));
    error.setMessage(getMessage(errorEnum.getCode(), params));
    error.setPath(path);
    return error;
  }

  private String getDescription(String errorCode, Object[] params) {
    return getMessageSourceMessage(String.format(ERROR_DESCRIPTION_FORMAT, errorCode), params);
  }

  private String getMessage(String errorCode, Object[] params) {
    return getMessageSourceMessage(String.format(ERROR_MESSAGE_FORMAT, errorCode), params);
  }

  private String getMessageSourceMessage(String code, Object[] params) {
    return String.format(accountMessageSource.getMessage(code), params);
  }
}
