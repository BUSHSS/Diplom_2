package api.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class DataCreateUser {


    private static final String USER_CREATE_ENDPOINT = "/api/auth/register";
    private static final String USER_AUTH_ENDPOINT = "/api/auth/user";
    private static final String USER_LOGIN_ENDPOINT = "/api/auth/login";
    private String email;
    private String password;
    private String name;

    // конструктор со всеми параметрами
    public DataCreateUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public DataCreateUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // конструктор без параметров
    public DataCreateUser() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Step("Шаг: Отправка запроса на создание пользователя /api/auth/register")
    public Response sendPostRegister(DataCreateUser dataCreateUser) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(dataCreateUser)
                .when()
                .post(USER_CREATE_ENDPOINT);
        return response;
    }

    @Step("Шаг: Проверка статус кода")
    public void compareStatusCode(Response response, Integer expectedResult) {
        response.then().assertThat().statusCode(expectedResult);
    }

    @Step("Шаг: Проверка тела неуспешного ответа")
    public void compareMessage(Response response, String expectedMessage) {
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Шаг: Сохранение accessToken при успешной регистрации /api/auth/register")
    public String saveAccessToken(Response response) {
        return response.then().extract().body().path("accessToken");
    }

    @Step("Шаг: Проверить, что нельзя создать пользователя с тем же email и тело ответа /api/auth/register")
    public void createCopy(DataCreateUser dataCreateUser, String expectedMessage) {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(dataCreateUser)
                .when()
                .post(USER_CREATE_ENDPOINT).then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Шаг: Удаление созданного пользователя")
    public void deleteData(String accessToken) {

        String accessTokenJson = "{\"accessToken\" : " + accessToken + "}";
        given()
                .header("Content-type", "application/json")
                .and().body(accessTokenJson)
                .delete(USER_AUTH_ENDPOINT);

    }


    @Step("Шаг: Проверка, что успешный ответ содержит токены /api/auth/login")
    public void checkToken(Response response) {
        response.then().assertThat().body("accessToken", notNullValue()).and().body("refreshToken", notNullValue());
    }

    @Step("Шаг: Изменить данные пользователя /api/auth/user с авторизацией и проверить ответ")
    public Response editUser(String accessToken, DataCreateUser dataCreateUser) {

        Response response = given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type", "application/json").and()
                .body(dataCreateUser)
                .when()
                .patch(USER_AUTH_ENDPOINT);
        return response;

    }

    @Step("Шаг: Отправка запроса на авторизацию /api/auth/login")
    public Response login(DataCreateUser dataCreateUser) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(dataCreateUser)
                .when()
                .post(USER_LOGIN_ENDPOINT);
        return response;
    }

    @Step("Шаг: Изменить данные пользователя /api/auth/user без авторизации и проверить ответ")
    public Response editUserWithoutToken(DataCreateUser dataCreateUser) {

        Response response = given().headers(
                        "Content-Type", "application/json").and()
                .body(dataCreateUser)
                .when()
                .patch(USER_AUTH_ENDPOINT);
        return response;

    }

}
