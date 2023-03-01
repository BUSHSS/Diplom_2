import api.user.DataCreateUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;


@RunWith(Parameterized.class)
public class TestCreateUser {


    private final String email;
    private final String password;
    private final String name;
    private final Integer expectedResult;
    private final String expectedMessage;
    private String accessToken;
    public TestCreateUser(String email, String password, String name, Integer expectedResult, String expectedMessage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Создание пользователя. Тестовые данные: {0} {1} {2} {3} {4}")
    public static Object[][] newUserData() {
        return new Object[][]{
                {"test-email" + new Random().nextInt(1000) + "@yandex.ru", "123456", null, 403, "Email, password and name are required fields"},
                {"test-email" + new Random().nextInt(1000) + "@yandex.ru", null, "Sergey", 403, "Email, password and name are required fields"},
                {null, "123456", "Sergey", 403, "Email, password and name are required fields"},
                {"test-email" + new Random().nextInt(1000) + "@yandex.ru", "123456", "Sergey", 200, "User already exists"},
        };
    }

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание пользователя /api/auth/register")
    public void сreateUser() {

        DataCreateUser dataCreateUser = new DataCreateUser(email, password, name);
        Response response = dataCreateUser.sendPostRegister(dataCreateUser);
        dataCreateUser.compareStatusCode(response, expectedResult);
        if (expectedResult == 403) {
            dataCreateUser.compareMessage(response, expectedMessage);
        } else if (expectedResult == 200) {
            accessToken = dataCreateUser.saveAccessToken(response);
            dataCreateUser.createCopy(dataCreateUser, expectedMessage);
            dataCreateUser.deleteData(accessToken);
        }
    }


}



