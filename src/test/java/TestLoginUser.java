import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestLoginUser {

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String email;
    private String password;
    private Integer expectedResult;
    private String expectedMessage;

    public TestLoginUser(String email, String password, Integer expectedResult, String expectedMessage) {
        this.email = email;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }
    @Parameterized.Parameters
    public static Object[][] newUserData(){
        return new Object[][] {
                {"test-email@gmail.com", null, 401, "email or password are incorrect"},
                {null, "123456", 401, "email or password are incorrect"},
                {"test-email@gmail.com", "123456", 200, null},
                {"test-email@gmail.com", "FalsePass", 401, "email or password are incorrect"}
        };
    }


    @Test
    @DisplayName("Проверка авторизации пользователя/api/v1/courier/login")
    public void LoginUser() {
        Response response = login();
        checkStatus(response);
        if (expectedResult==200) {
            checkToken(response);
        } else if (expectedResult==401) {
            checkMessage(response);
        }

    }

    @Step("Шаг: Отправка запроса на авторизацию /api/auth/login")
    public Response login(){
        DataLoginUser dataLoginUser = new DataLoginUser(email,password);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(dataLoginUser)
                .when()
                .post("/api/auth/login");
        return response;
    }

    @Step("Шаг: Проверка статус кода /api/auth/login")
    public void checkStatus(Response response){
        response.then().assertThat().statusCode(expectedResult);
    }

    @Step("Шаг: Проверка сообщения об ошибке /api/auth/login")
    public void checkMessage(Response response){
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }
    @Step("Шаг: Проверка, что успешный ответ содержит токены /api/auth/login")
    public void checkToken(Response response){
        response.then().assertThat().body("accessToken", notNullValue()).and().body("refreshToken", notNullValue());
    }


}
