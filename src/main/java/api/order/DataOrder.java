package api.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DataOrder {
    private static final String ORDERS_GET_ENDPOINT = "/api/orders";
    private static final String ORDERS_POST_ENDPOINT = "/api/orders";

    @Step("Шаг: Получить список заказов /api/orders с авторизацией и вернуть ответ")
    public Response getOrderWithToken(String accessToken) {

        Response response = given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type", "application/json").and()
                .when()
                .get(ORDERS_GET_ENDPOINT);
        return response;
    }


    @Step("Шаг: Получить список заказов /api/orders без авторизации и вернуть ответ")
    public Response getOrderWithoutToken() {

        Response response = given().headers(
                        "Content-Type", "application/json").and()
                .when()
                .get(ORDERS_GET_ENDPOINT);
        return response;
    }

    @Step("Шаг: Создать заказ /api/orders с авторизацией и вернуть ответ")
    public Response createOrderWithToken(String OrderJson, String accessToken) {

        Response response = given().headers(
                        "authorization",
                        "authorization" + accessToken,
                        "Content-Type", "application/json").and()
                .body(OrderJson)
                .when()
                .post(ORDERS_POST_ENDPOINT);
        return response;
    }


    @Step("Шаг: Создать заказ /api/orders без авторизации и вернуть ответ")
    public Response createOrderWithoutToken(String OrderJson) {

        Response response = given().headers(
                        "Content-Type", "application/json").and()
                .body(OrderJson)
                .when()
                .post(ORDERS_POST_ENDPOINT);
        return response;
    }
}
