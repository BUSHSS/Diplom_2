
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
public class TestEditProfile {



    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String emailUser = "email"+new Random().nextInt(1000)+"@yandex.ru";
    private String passwordUser = "password"+new Random().nextInt(1000);
    private String nameUser = "ivan"+new Random().nextInt(1000);
    private String email;
    private String password;
    private String name;
    private String accessToken;
    private Integer expectedResult;
    private String expectedMessage;
    private Boolean token;
    public TestEditProfile(Boolean token, String email, String password, String name, Integer expectedResult, String expectedMessage) {
        this.token =token;
        this.email = email;
        this.password = password;
        this.name=name;
        this.expectedResult=expectedResult;
        this.expectedMessage=expectedMessage;
    }
    @Parameterized.Parameters
    public static Object[][] newUserData(){
        return new Object[][] {
                {true, "test"+new Random().nextInt(1000)+"@yandex.ru", "123456", "Sergey", 200, null},
                {true, "test@yandex.ru", "123456", "Sergey", 403, "User with such email already exists"},
                {false, "test"+new Random().nextInt(1000)+"@yandex.ru", "123456", "Sergey", 401, "You should be authorised"},
        };
    }


    @Test
    @DisplayName("ИПроверка редактирования полей профиля /api/auth/user")
    public void editProfile() {

        DataCreateUser dataCreateUser = createUser();
        Response response = sendPostRegister(dataCreateUser);
        compareStatusCode(response);
        saveAccessToken(response);
        if (token == false) {
            editUserWithoutToken();

        }
        else if (token == true)
        {
            editUser();
        }

        deleteData(accessToken);

    }

    @Step("Шаг: генерация нового объекта dataCreateUser")
    public DataCreateUser createUser(){
        DataCreateUser dataCreateUser = new DataCreateUser(emailUser,passwordUser,nameUser);
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
        response.then().assertThat().statusCode(200);
    }


    @Step("Шаг: Сохранение accessToken при успешной регистрации /api/auth/register")
    public void saveAccessToken(Response response){
        this.accessToken = response.then().extract().body().path("accessToken");;
    }

    @Step("Шаг: Изменить данные пользователя /api/auth/user с авторизацией и проверить ответ")
    public void editUser(){
        DataCreateUser dataCreateUser = new DataCreateUser(email,password,name);

        given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type","application/json").and()
                .body(dataCreateUser)
                .when()
                .patch("/api/auth/user").then().assertThat().statusCode(expectedResult).and().body("message", equalTo(expectedMessage));

    }


    @Step("Шаг: Изменить данные пользователя /api/auth/user без авторизации и проверить ответ")
    public void editUserWithoutToken(){
        DataCreateUser dataCreateUser = new DataCreateUser(email,password,name);

        given().headers(
                        "Content-Type","application/json").and()
                .body(dataCreateUser)
                .when()
                .patch("/api/auth/user").then().assertThat().statusCode(expectedResult).and().body("message", equalTo(expectedMessage));

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

