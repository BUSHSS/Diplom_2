import api.user.DataCreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestLoginUser {

    private final String email;
    private final String password;
    private final Integer expectedResult;
    private final String expectedMessage;
    public TestLoginUser(String email, String password, Integer expectedResult, String expectedMessage) {
        this.email = email;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Проверка авторизации пользователя. Тестовые данные: {0} {1} {2} {3}")
    public static Object[][] newUserData() {
        return new Object[][]{
                {"test-email@gmail.com", null, 401, "email or password are incorrect"},
                {null, "123456", 401, "email or password are incorrect"},
                {"test-email@gmail.com", "123456", 200, null},
                {"test-email@gmail.com", "FalsePass", 401, "email or password are incorrect"}
        };
    }

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Проверка авторизации пользователя/api/v1/courier/login")
    public void LoginUser() {
        DataCreateUser dataCreateUser = new DataCreateUser(email, password);
        Response response = dataCreateUser.login(dataCreateUser);
        dataCreateUser.compareStatusCode(response, expectedResult);
        if (expectedResult == 200) {
            dataCreateUser.checkToken(response);
        } else if (expectedResult == 401) {
            dataCreateUser.compareMessage(response, expectedMessage);
        }

    }
}
