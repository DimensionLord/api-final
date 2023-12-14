package ru.karine.api.tests;


import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.karine.api.BasePetTest;
import ru.karine.api.dto.Pet;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Test(groups = {"pet", "petStore"})
public class PetTest extends BasePetTest {


    @DataProvider
    public Object[][] providePets() {
        return new Object[][]{
                {
                        pet()
                }
        };
    }

    @DataProvider
    public Object[][] provideTwoPets() {
        Long id = createId();
        return new Object[][]{
                {
                        pet(id, statuses[ThreadLocalRandom.current().nextInt(statuses.length)]),
                        pet(id, statuses[ThreadLocalRandom.current().nextInt(statuses.length)])
                }
        };
    }

    @DataProvider
    public Object[][] providePetsWithCustomStatus() {
        return new Object[][]{
                {
                        pet(createId(), UUID.randomUUID().toString())
                }
        };
    }

    @Test(description = "Проверка добавления питомца", dataProvider = "providePets")
    public void postPetTest(Pet pet) {
        postPet(pet);
        checkPet(pet);
    }

    @Test(description = "Проверка удаления питомца", dataProvider = "providePets")
    public void deletePetTest(Pet pet) {
        postPet(pet);
        delete(petIdSpec.pathParam("id", pet.getId()));
    }

    @Test(description = "Проверка обработки удаления несуществующего питомца", dataProvider = "providePets")
    public void deleteNonExistingPetTest(Pet pet) {
        deleteNonExisting(petIdSpec.pathParam("id", pet.getId()));
    }


    @Test(description = "Проверка обработки получения несуществующего питомца", dataProvider = "providePets")
    public void getNonExistingPetTest(Pet pet) {
        checkPetDoesNotExist(pet);
    }

    @Test(description = "Проверка обработки добавления двух питомцев с одинаковым Id", dataProvider = "provideTwoPets")
    public void postPetTwiceTest(Pet replacedPet, Pet pet) {
        postPet(replacedPet);
        postPet(pet);
        checkPet(pet);
    }

    @Test(description = "Проверка изменения информации о питомце", dataProvider = "provideTwoPets")
    public void postIdPetTest(Pet pet1, Pet pet2) {
        postPet(pet1);
        RestAssured
                .given(petIdSpec)
                .pathParam("id", pet1.getId())
                .formParam("status", pet2.getStatus())
                .formParam("name", pet2.getName())
                .when()
                .post()
                .then()
                .spec(baseResponseSpec);

        pet1.setName(pet2.getName());
        pet1.setStatus(pet2.getStatus());
        checkPet(pet1);
    }

    @Test(description = "Проверка добавления несуществующего питомца", dataProvider = "providePets")
    public void postNonExistingIdPetTest(Pet pet) {
        RestAssured
                .given(petIdSpec)
                .pathParam("id", pet.getId())
                .formParam("status", pet.getStatus())
                .formParam("name", pet.getName())
                .when()
                .post()
                .then()
                .spec(notFoundResponseSpec);
    }

    @Test(description = "Проверка добавления питомца с несуществующим статусом", dataProvider = "providePetsWithCustomStatus")
    public void postPetWithCustomStatusTest(Pet pet) {
        postPet(pet);
        Pet[] actualPets = RestAssured
                .given(petBaseSpec)
                .queryParam("status", pet.getStatus())
                .when()
                .get("/findByStatus")
                .then()
                .spec(baseResponseSpec)
                .extract().body().as(Pet[].class);
        Assert.assertEquals(actualPets.length, 1);
        Assert.assertEquals(actualPets[0], pet);
    }

    @Test(description = "Проверка получения питомца с несуществующим статусом", dataProvider = "providePetsWithCustomStatus")
    public void getPetWithNonExistingStatusTest(Pet pet) {

        Pet[] actualPets = RestAssured
                .given(petBaseSpec)
                .queryParam("status", pet.getStatus())
                .when()
                .get("/findByStatus")
                .then()
                .spec(baseResponseSpec)
                .extract().body().as(Pet[].class);
        Assert.assertEquals(actualPets.length, 0);
    }

    @Test(description = "Проверка добавления питомца через put", dataProvider = "providePets")
    public void putPetTest(Pet pet) {
        RestAssured
                .given(jsonPetBaseSpec)
                .body(pet)
                .when()
                .put()
                .then()
                .spec(baseResponseSpec);

        checkPet(pet);
    }


}
