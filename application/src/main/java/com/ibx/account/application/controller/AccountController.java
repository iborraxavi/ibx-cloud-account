package com.ibx.account.application.controller;

import com.ibx.account.application.api.AccountApi;
import com.ibx.account.application.model.RegisterRequest;
import com.ibx.account.application.model.RegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
public class AccountController implements AccountApi {

    @Override
    public Mono<ResponseEntity<RegisterResponse>> accountRegister(Mono<RegisterRequest> registerRequest, ServerWebExchange exchange) {
        final RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(new BigDecimal(1));
        registerResponse.setUsername("Username");

        return Mono.just(new ResponseEntity<>(registerResponse, HttpStatus.CREATED));
    }
}