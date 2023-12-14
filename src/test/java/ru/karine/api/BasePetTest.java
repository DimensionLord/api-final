package ru.karine.api;

import com.github.javafaker.Faker;
import io.restassured.specification.RequestSpecification;
import ru.karine.api.dto.Category;
import ru.karine.api.dto.Pet;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Вспомогательный класс для тестирования функционала /pet
 **/
public abstract class BasePetTest extends BaseTest {


    /**
     * Расширение спецификации запросов с указанием пути до endpoint-ов
     */
    protected final static RequestSpecification petBaseSpec = buildFromSpec(petStoreSpec)
            .setBasePath("v2/pet")
            .build();

    /**
     * Расширение спецификации запросов с указанием пути до endpoint-ов
     */
    protected final static RequestSpecification petIdSpec = buildFromSpec(petStoreSpec)
            .setBasePath("v2/pet/{id}")
            .build();

    /**
     * Расширение спецификации запросов с указанием header
     */
    protected final static RequestSpecification jsonPetBaseSpec = buildFromSpec(petBaseSpec)
            .addHeader("Content-Type", "application/json")
            .build();


    /**
     * Массив корректных статусов
     */
    protected final static String[] statuses = {"available", "pending", "sold"};


    /**
     * Метод создание pet с рандомными данными и фиксированными Id, и статус
     * @param petId
     * @param status
     * @return
     */
    protected Pet pet(Long petId, String status) {
        return Pet.builder()
                .withId(petId)
                .withCategory(new Category(Long.valueOf(Faker.instance().numerify("######")), Faker.instance().funnyName().name()))
                .withName(Faker.instance().pokemon().name())
                .withPhotoUrls(Collections.emptyList())
                .withTags(Collections.emptyList())
                .withStatus(status)
                .build();
    }

    /**
     * Метод, возвращающий случайный корректный статус
     */
    protected Pet pet() {
        return pet(createId(), statuses[ThreadLocalRandom.current().nextInt(statuses.length)]);
    }

    /**
     *  Метод добавления нового pet
     */
    protected void postPet(Pet pet){
        post(pet, jsonPetBaseSpec);
    }

    /**
     *  Метод проверки pet, полученного по Id
     */
    protected void checkPet(Pet pet){
        checkGetResult(pet, petIdSpec.pathParam("id", pet.getId()), Pet.class);
    }

    /**
     *  Метод проверки отсутствия pet по Id
     */
    protected void checkPetDoesNotExist(Pet pet) {
        checkGetResultDoesNotExist( petIdSpec.pathParam("id", pet.getId()));
    }

}
