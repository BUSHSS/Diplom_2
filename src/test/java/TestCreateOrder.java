
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class TestCreateOrder {



    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    private String emailUser = "testemail"+new Random().nextInt(1000)+"@yandex.ru";
    private String passwordUser = "password"+new Random().nextInt(1000);
    private String nameUser = "ivan"+new Random().nextInt(1000);
    private Boolean token;
    private Boolean isIngredient;
    private Integer expectedResult;
    private String expectedMessage;
    private String accessToken;
    private Response orderResponse;
    public TestCreateOrder(Boolean token, Boolean isIngredient, Integer expectedResult, String expectedMessage) {
        this.token =token;
        this.isIngredient = isIngredient;
        this.expectedResult=expectedResult;
        this.expectedMessage=expectedMessage;
    }
    @Parameterized.Parameters
    public static Object[][] newOrderData(){
        return new Object[][] {
                {true, true, 200, null},
                {true, false, 400, "Ingredient ids must be provided"},
                {false, true, 200, null},
                {false, false, 400, "Ingredient ids must be provided"},
                {true, true, 500, null},
        };
    }


    @Test
    @DisplayName("Создание задания /api/orders")
    public void сreateOrder() {

        DataCreateUser dataCreateUser = new DataCreateUser(emailUser,passwordUser,nameUser);
        Response response = sendPostRegister(dataCreateUser);
        compareStatusCode(response);
        saveAccessToken(response);

        JsonIngredients jsonIngredients = getIngredients();
        String OrderJson = saveIdIngredient(jsonIngredients);

        if (token == false) {
            orderResponse = createOrderWithoutToken(OrderJson);
        }
        else
        {
            orderResponse = createOrderWithToken(OrderJson);
        }

        if (expectedResult == 500)
        {
            checkStatus(orderResponse);
        } else  {
            checkStatus(orderResponse);
            checkMessage(orderResponse);
        }


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

    @Step("Шаг: Получить список ингредиентов")
    public JsonIngredients getIngredients(){
        JsonIngredients jsonIngredients = given()
                .header("Content-type", "application/json")
                .get("/api/ingredients")
                .body().as(JsonIngredients.class);
        return jsonIngredients;
    }
    @Step("Шаг: Получить ингредиент из списка и сформировать json для передачи в заказ")
    public String saveIdIngredient(JsonIngredients jsonIngredients){
        List<Data> s = jsonIngredients.getData();
        Data data = s.get(0);
        String idIngredient = data.get_id();
        String OrderJson;
        if (isIngredient == false) {
            OrderJson="{\"ingredients\" : []}";;
        } else if (expectedResult==500){
            OrderJson="{\"ingredients\" : [\""+idIngredient+"123"+"\"]}";
        } else {
            OrderJson="{\"ingredients\" : [\""+idIngredient+"\"]}";
        }
        return OrderJson;
    }

    @Step("Шаг: Создать заказ /api/orders с авторизацией и вернуть ответ")
    public Response createOrderWithToken(String OrderJson){

        Response response =   given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type", "application/json").and()
                .body(OrderJson)
                .when()
                .post("/api/orders");
        return response;
    }


    @Step("Шаг: Создать заказ /api/orders без авторизации и вернуть ответ")
    public Response createOrderWithoutToken(String OrderJson){

        Response response = given().headers(
                        "Content-Type","application/json").and()
                .body(OrderJson)
                .when()
                .post("/api/orders");
        return response;
    }

    @Step("Шаг: Проверка статус кода")
    public void checkStatus(Response response){
        response.then().assertThat().statusCode(expectedResult);
    }

    @Step("Шаг: Проверка сообщения об ошибке")
    public void checkMessage(Response response){
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Шаг: Удаление созданного пользователя")
    public void deleteData(String accessToken) {

        String accessTokenJson="{\"accessToken\" : "+accessToken+"}";
        given()
                .header("Content-type", "application/json")
                .and().body(accessTokenJson)
                .delete("/api/auth/user");

    }
    @After
    public void out()
    {
        deleteData(accessToken);
    }

}



