import api.order.DataOrder;
import api.user.DataCreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestGetOrders {
    private final Boolean token;
    private final String email;
    private final String password;
    private final Integer expectedResult;
    private final String expectedMessage;
    private String accessToken;
    private Response responseOrder;
    public TestGetOrders(Boolean token, String email, String password, Integer expectedResult, String expectedMessage) {
        this.token = token;
        this.email = email;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Получение заказов пользователя. Тестовые данные: {0} {1} {2} {3} {4}")
    public static Object[][] newUserData() {
        return new Object[][]{
                {true, "test-email@gmail.com", "123456", 200, null},
                {false, "test-email@gmail.com", "123456", 401, "You should be authorised"}
        };
    }

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Получение заказов пользователя /api/orders")
    public void getOrders() {
        DataCreateUser dataCreateUser = new DataCreateUser(email, password);
        DataOrder dataOrder = new DataOrder();
        if (token) {
            Response response = dataCreateUser.login(dataCreateUser);
            dataCreateUser.compareStatusCode(response, expectedResult);
            accessToken = dataCreateUser.saveAccessToken(response);
            responseOrder = dataOrder.getOrderWithToken(accessToken);
        } else if (!token) {
            responseOrder = dataOrder.getOrderWithoutToken();
        }
        dataCreateUser.compareStatusCode(responseOrder, expectedResult);
        dataCreateUser.compareMessage(responseOrder, expectedMessage);
    }
}




