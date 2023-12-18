package com.ibx.account.application.controller;

import com.ibx.account.application.controller.utils.MongoDataUtils;
import com.ibx.account.application.model.RegisterRequestDto;
import com.ibx.account.domain.model.errors.ErrorsEnum;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(properties = "spring.profiles.active=test")
class AccountControllerIT {

  private static final String ACCOUNT_ID_01 = "6580a416f731564f67e1e213";

  private static final String ACCOUNT_ID_02 = "6580a416f731564f67e1e214";

  private static final String ACCOUNT_USERNAME = "Username";

  private static final String ACCOUNT_PASSWORD = "Password";

  private static final String ACCOUNT_FIRST_NAME = "FirstName";

  private static final String ACCOUNT_LAST_NAME = "LastName";

  private static final String ACCOUNT_BASE_PATH = "http://localhost:8090/ibx/1/account";

  private static final String ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH = "http://localhost:8090/ibx/1/account/{accountId}";

  private static WebTestClient webTestClient;

  @Autowired
  private MongoDataUtils mongoDataUtils;

  @BeforeAll
  static void setup(ApplicationContext applicationContext) {
    webTestClient = WebTestClient
        .bindToApplicationContext(applicationContext)
        .build();
  }

  @BeforeEach
  void beforeEach() throws IOException {
    mongoDataUtils.initDb();
  }

  @Test
  @DisplayName("Find all accounts when success should return all accounts")
  void findAllAccounts_whenSuccess_shouldReturnAllAccounts() {
    webTestClient.get().uri(ACCOUNT_BASE_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(2)
        .jsonPath("$[0].id").isEqualTo(ACCOUNT_ID_01)
        .jsonPath("$[0].username").isEqualTo("Username1")
        .jsonPath("$[0].firstName").isEqualTo("FirstName1")
        .jsonPath("$[0].lastName").isEqualTo("LastName1")
        .jsonPath("$[1].id").isEqualTo(ACCOUNT_ID_02)
        .jsonPath("$[1].username").isEqualTo("Username2")
        .jsonPath("$[1].firstName").isEqualTo("FirstName2")
        .jsonPath("$[1].lastName").isEqualTo("LastName2");
  }

  @Test
  @DisplayName("Account register when null username should return expected error")
  void accountRegister_whenNullUsername_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(null, ACCOUNT_PASSWORD,
        ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.ACCOUNT_REGISTER_USERNAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Account register when null password should return expected error")
  void accountRegister_whenNullPassword_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME, null,
        ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.ACCOUNT_REGISTER_PASSWORD_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Account register when null first name should return expected error")
  void accountRegister_whenNullFirstName_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, null, ACCOUNT_LAST_NAME);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.ACCOUNT_REGISTER_FIRST_NAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Account register when null last name should return expected error")
  void accountRegister_whenNullLastName_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, null);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.ACCOUNT_REGISTER_LAST_NAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Account register when username already exists should return expected error")
  void accountRegister_whenUsernameAlreadyExists_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto("Username1",
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.CONFLICT.value())
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(ErrorsEnum.ACCOUNT_REGISTER_USERNAME_ALREADY_EXISTS.getCode());
  }

  @Test
  @DisplayName("Account register when success should return expected account")
  void accountRegister_whenSuccess_shouldReturnExpectedAccount() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.post().uri(ACCOUNT_BASE_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isNotEmpty()
        .jsonPath("$.username").isEqualTo(registerRequestDto.getUsername())
        .jsonPath("$.firstName").isEqualTo(registerRequestDto.getFirstName())
        .jsonPath("$.lastName").isEqualTo(registerRequestDto.getLastName());
  }

  @Test
  @DisplayName("Find account by id when account not found should return expected error")
  void findAccountById_whenAccountNotFound_shouldReturnExpectedError() {
    webTestClient.get().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, UUID.randomUUID().toString())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.ACCOUNT_NOT_FOUND.getCode());
  }

  @Test
  @DisplayName("Find account by id when success should return expected account")
  void findAccountById_whenSuccess_shouldReturnExpectedAccount() {
    webTestClient.get().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(ACCOUNT_ID_01)
        .jsonPath("$.username").isEqualTo("Username1")
        .jsonPath("$.firstName").isEqualTo("FirstName1")
        .jsonPath("$.lastName").isEqualTo("LastName1");
  }

  @Test
  @DisplayName("Update account when null username should return expected error")
  void updateAccount_whenNullUsername_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(null, ACCOUNT_PASSWORD,
        ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.UPDATE_ACCOUNT_USERNAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Update account when null password should return expected error")
  void updateAccount_whenNullPassword_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME, null,
        ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.UPDATE_ACCOUNT_PASSWORD_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Update account when null first name should return expected error")
  void updateAccount_whenNullFirstName_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, null, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.UPDATE_ACCOUNT_FIRST_NAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Update account when null last name should return expected error")
  void updateAccount_whenNullLastName_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, null);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorsEnum.UPDATE_ACCOUNT_LAST_NAME_REQUIRED.getCode());
  }

  @Test
  @DisplayName("Update account when username already exists should return expected error")
  void updateAccount_whenUsernameAlreadyExists_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto("Username2",
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.CONFLICT.value())
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(ErrorsEnum.UPDATE_ACCOUNT_USERNAME_ALREADY_EXISTS.getCode());
  }

  @Test
  @DisplayName("Update account when account not found should return expected error")
  void updateAccount_whenAccountNotFound_shouldReturnExpectedError() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, UUID.randomUUID().toString())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(ErrorsEnum.ACCOUNT_NOT_FOUND.getCode());
  }

  @Test
  @DisplayName("Update account when success should return expected account")
  void updateAccount_whenSuccess_shouldReturnExpectedAccount() {
    final RegisterRequestDto registerRequestDto = getRegisterRequestDto(ACCOUNT_USERNAME,
        ACCOUNT_PASSWORD, ACCOUNT_FIRST_NAME, ACCOUNT_LAST_NAME);

    webTestClient.put().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(registerRequestDto), RegisterRequestDto.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isNotEmpty()
        .jsonPath("$.username").isEqualTo(registerRequestDto.getUsername())
        .jsonPath("$.firstName").isEqualTo(registerRequestDto.getFirstName())
        .jsonPath("$.lastName").isEqualTo(registerRequestDto.getLastName());
  }

  @Test
  @DisplayName("Delete account when account not found should return expected error")
  void deleteAccount_whenAccountNotFound_shouldReturnExpectedError() {
    webTestClient.delete().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, UUID.randomUUID().toString())
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(ErrorsEnum.ACCOUNT_NOT_FOUND.getCode());
  }

  @Test
  @DisplayName("Delete account when success should return expected error")
  void deleteAccount_whenSuccess_shouldReturnExpectedError() {
    webTestClient.delete().uri(ACCOUNT_WITH_ACCOUNT_ID_BASE_PATH, ACCOUNT_ID_01)
        .exchange()
        .expectStatus().isNoContent();
  }

  private RegisterRequestDto getRegisterRequestDto(final String username, final String password,
      final String firstName, final String lastName) {
    final var registerRequestDto = new RegisterRequestDto();
    registerRequestDto.setUsername(username);
    registerRequestDto.setPassword(password);
    registerRequestDto.setFirstName(firstName);
    registerRequestDto.setLastName(lastName);
    return registerRequestDto;
  }

}
