import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class TestGetOrders {
    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    private Boolean token;
    private String email;
    private String password;
    private Integer expectedResult;
    private String expectedMessage;
    private String accessToken;
    private Response responseOrder;
    public TestGetOrders(Boolean token, String email, String password, Integer expectedResult, String expectedMessage) {
        this.token = token;
        this.email = email;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }
    @Parameterized.Parameters
    public static Object[][] newUserData(){
        return new Object[][] {
                {true, "test-email@gmail.com", "123456", 200, null},
                {false, "test-email@gmail.com", "123456", 401, "You should be authorised"}
        };
    }


    @Test
    @DisplayName("Получение заказов пользователя /api/orders")
    public void getOrders() {
        if (token==true) {
            Response response = login();
            checkStatus(response);
            saveAccessToken(response);
            responseOrder = getOrderWithToken();
        } else if (token==false) {
            responseOrder = getOrderWithoutToken();
        }
        checkStatus(responseOrder);
        checkMessage(responseOrder);
    }

    @Step("Шаг: Отправка запроса на авторизацию /api/auth/login с проверкой кода")
    public Response login(){
        DataLoginUser dataLoginUser = new DataLoginUser(email,password);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(dataLoginUser)
                .when()
                .post("/api/auth/login");
        response.then().assertThat().statusCode(200);;
        return response;
    }

    @Step("Шаг: Сохранение accessToken при успешной авторизации /api/auth/login")
    public void saveAccessToken(Response response){
        this.accessToken = response.then().extract().body().path("accessToken");

    }
    @Step("Шаг: Получить список заказов /api/orders с авторизацией и вернуть ответ")
    public Response getOrderWithToken(){

        Response response =  given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type", "application/json").and()
                .when()
                .get("/api/orders");
        return response;
    }


    @Step("Шаг: Получить список заказов /api/orders без авторизации и вернуть ответ")
    public Response getOrderWithoutToken(){

        Response response = given().headers(
                        "Content-Type","application/json").and()
                .when()
                .get("/api/orders");
        return response;
    }
    @Step("Шаг: Проверка статус кода /api/orders")
    public void checkStatus(Response response){
        response.then().assertThat().statusCode(expectedResult);
    }

    @Step("Шаг: Проверка сообщения об ошибке /api/orders")
    public void checkMessage(Response response){
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }


}
