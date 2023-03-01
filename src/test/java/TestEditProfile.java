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
public class TestEditProfile {


    private final String emailUser = "email" + new Random().nextInt(1000) + "@yandex.ru";
    private final String passwordUser = "password" + new Random().nextInt(1000);
    private final String nameUser = "ivan" + new Random().nextInt(1000);
    private final String email;
    private final String password;
    private final String name;
    private String accessToken;
    private final Integer expectedResult;
    private final String expectedMessage;
    private final Boolean token;
    private Response responseChange;
    DataCreateUser dataCreateUser;
    public TestEditProfile(Boolean token, String email, String password, String name, Integer expectedResult, String expectedMessage) {
        this.token = token;
        this.email = email;
        this.password = password;
        this.name = name;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    @Parameterized.Parameters(name = "Редактирование профиля. Тестовые данные: {0} {1} {2} {3} {4} {5}")
    public static Object[][] newUserData() {
        return new Object[][]{
                {true, "test" + new Random().nextInt(1000) + "@yandex.ru", "123456", "Sergey", 200, null},
                {true, "test@yandex.ru", "123456", "Sergey", 403, "User with such email already exists"},
                {false, "test" + new Random().nextInt(1000) + "@yandex.ru", "123456", "Sergey", 401, "You should be authorised"},
        };
    }

    @Before
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Проверка редактирования полей профиля /api/auth/user")
    public void editProfile() {
        dataCreateUser = new DataCreateUser(emailUser, passwordUser, nameUser);
        Response response = dataCreateUser.sendPostRegister(dataCreateUser);
        accessToken = dataCreateUser.saveAccessToken(response);
        DataCreateUser changeDataUser = new DataCreateUser(email, password, name);
        responseChange = (token) ? changeDataUser.editUser(accessToken, changeDataUser) : changeDataUser.editUserWithoutToken(changeDataUser);
        changeDataUser.compareStatusCode(responseChange, expectedResult);
        changeDataUser.compareMessage(responseChange, expectedMessage);
    }
    @After
    public void out()
    {
        dataCreateUser.deleteData(accessToken);
    }
}

