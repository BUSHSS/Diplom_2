import api.ingredient.DataIngredient;
import api.order.DataOrder;
import api.user.DataCreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;


@RunWith(Parameterized.class)
public class TestCreateOrder {

    DataIngredient dataIngredient = new DataIngredient();
    DataOrder dataOrder = new DataOrder();
    private final String emailUser = "testemail" + new Random().nextInt(1000) + "@yandex.ru";
    private final String passwordUser = "password" + new Random().nextInt(1000);
    private final String nameUser = "ivan" + new Random().nextInt(1000);
    private final Boolean token;
    private final Boolean isIngredient;
    private final Integer expectedResult;
    private final String expectedMessage;
    private String accessToken;
    private Response orderResponse;
    DataCreateUser dataCreateUser;
    public TestCreateOrder(Boolean token, Boolean isIngredient, Integer expectedResult, String expectedMessage) {
        this.token = token;
        this.isIngredient = isIngredient;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Создание заказа. Тестовые данные: {0} {1} {2} {3}")
    public static Object[][] newOrderData() {
        return new Object[][]{
                {true, true, 200, null},
                {true, false, 400, "Ingredient ids must be provided"},
                {false, true, 200, null},
                {false, false, 400, "Ingredient ids must be provided"},
                {true, true, 500, null},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание задания /api/orders")
    public void сreateOrder() {
        dataCreateUser = new DataCreateUser(emailUser, passwordUser, nameUser);
        Response response = dataCreateUser.sendPostRegister(dataCreateUser);
        accessToken = dataCreateUser.saveAccessToken(response);
        String OrderJson = dataIngredient.saveIdIngredient(dataIngredient.getIngredients(), isIngredient, expectedResult);
        orderResponse = (token) ? dataOrder.createOrderWithToken(OrderJson, accessToken) : dataOrder.createOrderWithoutToken(OrderJson);
        if (expectedResult == 500) {
            dataCreateUser.compareStatusCode(orderResponse, expectedResult);
        } else {
            dataCreateUser.compareStatusCode(orderResponse, expectedResult);
            dataCreateUser.compareMessage(orderResponse, expectedMessage);
        }
    }

    @After
    public void out()
    {
        dataCreateUser.deleteData(accessToken);
    }
}




