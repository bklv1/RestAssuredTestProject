import static io.restassured.RestAssured.*; //here import it like this to get the given(), when(), then()

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class Test1 {

    @BeforeEach
    public void setup(){
        RestAssured.baseURI = "https://reqres.in/api";
    }


//    @Test
//    public void TestGet(){
//    Response response = RestAssured.get(baseUrl+ "/api/users?page=2");
//        System.out.println("Response: " + response.asString());
//        System.out.println("Status Code: " + response.statusCode());
//        System.out.println("Body: " + response.body().asPrettyString());
//        System.out.println("Time: " + response.getTime());
//    }

// after importing the static Rest Assured lib

    @Test
    public void TestGetGiven(){
        Response response=   given().contentType(ContentType.JSON).param("page","2")
                .when()
                .get("/users")
                .then()
                .extract().response();
//        System.out.println(response.body().asPrettyString());
        Assertions.assertEquals(200, response.statusCode());

    }

    public static String requestBody="{\n" +
            "\"name\": \"tsvetomir\",\n" +
            "\"job\": \"qa\"\n" +
            "}";
    public static String putRequestBody="{\n" +
            "\"name\": \"ceco\",\n" +
            "\"job\": \"dev\"\n" +
            "}";
    public Response responsePost(){
     return      given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/users").then().extract().response();
    }
    @Test
    public void postRequest() {
        Response response = responsePost();
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("tsvetomir", response.jsonPath().getString("name"));
        Assertions.assertEquals("qa", response.jsonPath().getString("job"));
        String date= response.jsonPath().getString("createdAt");
        int lastIndex = date.indexOf("T");
        String assertDate = date.substring(0, lastIndex);
        String dateToday = LocalDate.now().toString();
        Assertions.assertEquals(dateToday, assertDate);
    }

    @Test
    public void putRequest() {
        Response post = responsePost();


        String id= post.jsonPath().getString("id");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(putRequestBody)
                .when()
                .put("/users/"+ id)
                .then()
                .extract().response();
        String responseBody = response.body().asString();
        Gson gson = new Gson();

     PUTUserDTO dto=  gson.fromJson(response.body().asString(), PUTUserDTO.class);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("ceco", response.jsonPath().getString("name"));
        Assertions.assertEquals("dev", response.jsonPath().getString("job"));
    }
}
