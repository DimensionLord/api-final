package ru.karine.api;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;
import ru.karine.api.dto.User;

import java.util.List;

/**
 * Вспомогательный класс для тестирования функционала /user
 **/
public abstract class BaseUserTest extends BaseTest {

    /**
     * Расширение спецификации запросов с указанием пути до endpoint-ов
     */
    protected final static RequestSpecification userBaseSpec = buildFromSpec(petStoreSpec)
            .setBasePath("v2/user")
            .build();
    /**
     * Расширение спецификации запросов с указанием пути до endpoint-ов
     */
    protected final static RequestSpecification userUsernameSpec = buildFromSpec(petStoreSpec)
            .setBasePath("v2/user/{username}")
            .build();

    /**
     * Расширение спецификации запросов с указанием header
     */
    protected final static RequestSpecification jsonUserBaseSpec = buildFromSpec(userBaseSpec)
            .addHeader("Content-Type", "application/json")
            .build();

    /**
     * Метод создание user с рандомными данными
     */
    protected User user() {
        return User.builder()
                .withId(createId())
                .withUsername(Faker.instance().name().username() + Faker.instance().name().username())
                .withFirstName(Faker.instance().name().firstName())
                .withLastName(Faker.instance().name().lastName())
                .withEmail(Faker.instance().internet().emailAddress())
                .withPassword(Faker.instance().internet().password())
                .withPhone(Faker.instance().phoneNumber().phoneNumber())
                .withUserStatus(0)
                .build();
    }

    /**
     *  Метод добавления нового user
     */
    protected void postUser(User user) {
        post(user, jsonUserBaseSpec);
    }

    /**
     *  Метод проверки user, полученного по username
     */
    protected void checkUser(User user) {
        checkGetResult(user, userUsernameSpec.pathParam("username", user.getUsername()), User.class);
    }

    /**
     *  Метод проверки отсутствия user по username
     */
    protected void checkUserDoesNotExist(User user) {
        checkGetResultDoesNotExist(userUsernameSpec.pathParam("username", user.getUsername()));
    }

    /**
     * Метод добавления двух новых users
     * @param user1
     * @param user2
     */
    protected void postUsersArray(User user1, User user2) {
        RestAssured
                .given(jsonUserBaseSpec)
                .body(List.of(user1, user2))
                .when()
                .post("/createWithArray")
                .then()
                .spec(baseResponseSpec);

    }
}