package ru.karine.api;

import com.github.javafaker.Faker;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

/**
 * Вспомогательный класс. Общий предок всех тестовых классов
 */
public abstract class BaseTest {

    /**
     * Общая спецификация запросов.
     * Добавляет логирование для всех запросов как в консоль, так и в allure отчет.
     * Устанавлявает базовый endpoint
     */
    protected final static RequestSpecification petStoreSpec = new RequestSpecBuilder()
            .log(LogDetail.ALL)
            .addFilter(new AllureRestAssured())
            .setBaseUri("https://petstore.swagger.io/")
            .build();

    /**
     * Общая спецификация для проверки ответов.
     * Добавляет логирование для всех ответов в консоль.
     * Проверяет статус 200
     */
    protected final static ResponseSpecification baseResponseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .expectStatusCode(200)
            .build();

    /**
     * Общая спецификация для проверки ответов.
     * Добавляет логирование для всех ответов в консоль.
     * Проверяет статус 404
     */
    protected final static ResponseSpecification notFoundResponseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .expectStatusCode(404)
            .build();

    /**
     * Метод, который упрощает совмещение спецификации запросов
     * @param requestSpecification базовая спецификация
     * @return новая спецификация со всеми свойствами базовой
     */
    protected static RequestSpecBuilder buildFromSpec(RequestSpecification requestSpecification) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpecification);
    }

    /**
     * Метод, создающий уникальный числовой Id
     */
    protected Long createId() {
        return Long.valueOf(Faker.instance().numerify("########"));
    }

    /**
     * Общий метод для отправки post-запросов
     * @param object объект тела
     * @param spec спецификация запроса
     */
    protected void post(Object object, RequestSpecification spec) {
        RestAssured
                .given(spec)
                .body(object)
                .when()
                .post()
                .then()
                .spec(baseResponseSpec);
    }

    /**
     * Общий метод для отправки get-запросов и сравнения ответа с ожидаемым
     * @param object эталонный объект
     * @param spec спецификация запроса
     * @param clazz класс для десериализации ответа
     */
    protected void checkGetResult(Object object, RequestSpecification spec, Class<?> clazz) {
        Object actual = RestAssured
                .given(spec)
                .when()
                .get()
                .then()
                .spec(baseResponseSpec)
                .extract().body().as(clazz);
        Assert.assertEquals(actual, object);

    }

    /**
     * Общий метод для отправки get-запросов к несуществующим ресурсам
     * @param spec спецификация запроса
     */
    protected void checkGetResultDoesNotExist(RequestSpecification spec) {
        RestAssured
                .given(spec)
                .when()
                .get()
                .then()
                .spec(notFoundResponseSpec);
    }

    /**
     * Общий метод для отправки delete-запросов
     * @param spec спецификация запроса
     */
    protected void delete(RequestSpecification spec) {
        RestAssured
                .given(spec)
                .when()
                .delete()
                .then()
                .spec(baseResponseSpec);
    }

    /**
     * Общий метод для отправки delete-запросов к несуществующим ресурсам
     * @param spec спецификация запроса
     */
    protected void deleteNonExisting(RequestSpecification spec) {
        RestAssured
                .given(spec)
                .when()
                .delete()
                .then()
                .spec(notFoundResponseSpec);
    }

}
