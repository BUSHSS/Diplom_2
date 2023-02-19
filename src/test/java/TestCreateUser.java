
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class TestCreateUser {



    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String email;
    private String password;
    private String name;
    private Integer expectedResult;
    private String expectedMessage;
    private String accessToken;
    public TestCreateUser(String email, String password, String name, Integer expectedResult, String expectedMessage) {
        this.email = email;
        this.password = password;
        this.name=name;
        this.expectedResult=expectedResult;
        this.expectedMessage=expectedMessage;
    }
    @Parameterized.Parameters
    public static Object[][] newUserData(){
        return new Object[][] {
                {"test-email", "123456", null, 403, "Email, password and name are required fields"},
                {"test-email", null, "Sergey", 403, "Email, password and name are required fields"},
                {null, "123456", "Sergey", 403, "Email, password and name are required fields"},
                {"test-email", "123456", "Sergey", 200, "User already exists"},
        };
    }


    @Test
    @DisplayName("Создание пользователя /api/auth/register")
    public void сreateUser() {

        DataCreateUser dataCreateUser = newEmail();
        Response response = sendPostRegister(dataCreateUser);
        compareStatusCode(response);
        if (expectedResult==403)
        {
            compareMessage(response);
        }
        else if (expectedResult==200)
        {
            saveAccessToken(response);
            createCopy(dataCreateUser);
            deleteData(accessToken);
        }
    }

    @Step("Шаг: генерация нового email")
    public DataCreateUser newEmail(){
        if (email != null) {
            email=email+new Random().nextInt(1000)+"@yandex.ru";}
        DataCreateUser dataCreateUser = new DataCreateUser(email,password,name);
        return dataCreateUser;
    }

    @Step("Шаг: Отправка запроса на создание пользователя /api/auth/register")
    public Response sendPostRegister(DataCreateUser dataCreateUser){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(dataCreateUser)
                .when()
                .post("/api/auth/register");
        return response;
    }

    @Step("Шаг: Проверка статус кода /api/auth/register")
    public void compareStatusCode(Response response){
        response.then().assertThat().statusCode(expectedResult);
    }

    @Step("Шаг: Проверка тела неуспешного ответа /api/auth/register")
    public void compareMessage(Response response){
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Шаг: Сохранение accessToken при успешной регистрации /api/auth/register")
    public void saveAccessToken(Response response){
        this.accessToken = response.then().extract().body().path("accessToken");;
    }

    @Step("Шаг: Проверить, что нельзя создать пользователя с тем же email и тело ответа /api/auth/register")
    public void createCopy(DataCreateUser dataCreateUser){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(dataCreateUser)
                .when()
                .post("/api/auth/register").then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Шаг: Удаление созданного пользователя")
    public void deleteData(String accessToken) {

        String accessTokenJson="{\"accessToken\" : "+accessToken+"}";
        given()
                .header("Content-type", "application/json")
                .and().body(accessTokenJson)
                .delete("/api/auth/user");

    }
}



