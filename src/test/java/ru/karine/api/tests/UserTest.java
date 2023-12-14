package ru.karine.api.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.karine.api.BaseUserTest;
import ru.karine.api.dto.User;

@Test(groups = {"user", "petStore"})
public class UserTest extends BaseUserTest {
    @DataProvider
    public Object[][] provideUsers() {
        return new Object[][]{
                {user()}

        };
    }

    @DataProvider
    public Object[][] provideTwoUsers() {
        return new Object[][]{
                {
                        user(),
                        user()
                }
        };
    }

    @Test(description = "Проверка добавления пользователя", dataProvider = "provideUsers")
    public void postUserTest(User user) {
        postUser(user);
        checkUser(user);
    }

    @Test(description = "Проверка обработки получения несуществующего пользователя", dataProvider = "provideUsers")
    public void getNonExistingUserTest(User user) {
        checkUserDoesNotExist(user);
    }

    @Test(description = "Проверка авторизации пользователя", dataProvider = "provideUsers")
    public void loginUserTest(User user) {
        postUser(user);

        String responseMessage = RestAssured
                .given(userBaseSpec)
                .queryParam("username", user.getUsername())
                .queryParam("password", user.getPassword())
                .when()
                .get("/login")
                .then()
                .spec(baseResponseSpec)
                .extract().body().jsonPath().getString("message");
        Assert.assertTrue(responseMessage.matches("logged in user session:\\d{13}"), "session was not created or was invalid");
    }

    @Test(description = "Проверка выхода пользователя")
    public void logoutUserTest() {
        RestAssured
                .given(userBaseSpec)
                .when()
                .get("/logout")
                .then()
                .spec(baseResponseSpec);
    }

    @Test(description = "Проверка удаления пользователя", dataProvider = "provideUsers")
    public void deleteUserTest(User user) {
        postUser(user);
        delete(userUsernameSpec.pathParam("username", user.getUsername()));
        checkUserDoesNotExist(user);
    }

    @Test(description = "Проверка обработки удаления несуществующего пользователя")
    public void deleteNonExistingUserTest() {
        deleteNonExisting(userUsernameSpec.pathParam("username", Faker.instance().name().username()));
    }

    @Test(description = "Проверка изменения данных существующего пользователя", dataProvider = "provideUsers")
    public void putExistingUserTest(User user) {
        postUser(user);

        RestAssured
                .given(jsonUserBaseSpec)
                .body(user)
                .pathParam("username", user.getUsername())
                .when()
                .put("/{username}")
                .then()
                .spec(baseResponseSpec);

        checkUser(user);


    }

    @Test(description = "Проверка добавления нескольких пользователей", dataProvider = "provideTwoUsers")
    public void postUsersWithArrayTest(User user1, User user2) {
        postUsersArray(user1, user2);
        checkUser(user1);
        checkUser(user2);
    }

    @Test(description = "Проверка двух пользователей с одинаковым Id", dataProvider = "provideTwoUsers")
    public void postUsersSameIdWithArrayTest(User user1, User user2) {
        user2.setId(user1.getId());
        postUsersArray(user1, user2);

        checkUser(user2);
        checkUserDoesNotExist(user1);

    }

    @Test(description = "Проверка добавления двух пользователей с одинаковым именем пользователя", dataProvider = "provideTwoUsers")
    public void postUsersSameUsernameWithArrayTest(User user1, User user2) {
        user2.setUsername(user1.getUsername());
        postUsersArray(user1, user2);
        checkUser(user1);
        delete(userUsernameSpec.pathParam("username", user1.getUsername()));
        checkUser(user2);

    }
}
