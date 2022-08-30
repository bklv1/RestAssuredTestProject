import static io.restassured.RestAssured.*; //here import it like this to get the given(), when(), then()

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class Test1 {

    private Gson gson;
    @BeforeEach
    public void setup(){
       RestAssured.baseURI = "https://reqres.in/api";
       gson = new Gson();
    }
    @Test
    public void TestGet(){
    Response response = RestAssured.get("/users/2");
    Assertions.assertEquals(200, response.statusCode());
//        System.out.println(response.body().asPrettyString());
//        System.out.println(response.body().asString());
//
//          System.out.println("Response: " + response.asString());
//          System.out.println("Response: " + response.asPrettyString());
//        System.out.println("Status Code: " + response.statusCode());
//        System.out.println("Body: " + response.body().asPrettyString());
//        System.out.println("Time: " + response.getTime());
    }

// after importing the static Rest Assured lib

    @Test
    public void TestGetGiven(){
        Response response=
                given()
                .when()
                .get("/users/2")
                .then().extract().response();
                Assertions.assertEquals(200, response.statusCode());
    }

    public static String username= "tsvetomir";
    public static String job = "qa";
    public static String putUsername= "ceco";
    public static String putJob = "builder";
    public static String requestBody="{" +
            "\"name\": \""+username+"\"," +
            "\"job\": \""+job+"\"" +
            "}";
    public static String putRequestBody="{" +
            "\"name\": \""+putUsername+"\"," +
            "\"job\": \""+putJob+"\"" +
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

//        Assertions.assertEquals(201, response.statusCode());
//        Assertions.assertEquals(username, response.jsonPath().getString("name"));
//        Assertions.assertEquals(job, response.jsonPath().getString("job"));
//        String date= response.jsonPath().getString("createdAt");
//        int lastIndex = date.indexOf("T");
//        String assertDate = date.substring(0, lastIndex);
//        String dateToday = LocalDate.now().toString();
//        Assertions.assertEquals(dateToday, assertDate);
        System.out.println(response.body().asPrettyString());
        Gson deserizalize = new Gson();
        PostUserDTO dto = deserizalize.fromJson(response.body().asString(),PostUserDTO.class);
        System.out.println(dto.name);
    }

    @Test
    public void putRequest() {
        Response testing = responsePost();
        String id= testing.jsonPath().getString("id");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(putRequestBody)
                .when()
                .put("/users/"+id)
                .then()
                .extract().response();
        String responseBody = response.body().asString();
        System.out.println(responseBody);

//
     PUTUserDTO dto=  gson.fromJson(response.body().asString(), PUTUserDTO.class);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(putUsername, response.jsonPath().getString("name"));
        Assertions.assertEquals(putJob, response.jsonPath().getString("job"));
    }

    @Test
    public void DeleteUser(){
        Response posted = responsePost();
        String id= posted.body().jsonPath().getString("id");
        Response response = given().when().delete("/users/"+id).then().statusCode(204).extract().response();
    }

    @Test
    public void GetListOfUsers(){
        Response response = given().param("page","2").when().get("/users").then().extract().response();
        GetListOfUsersDTO get = gson.fromJson(response.body().asString(),GetListOfUsersDTO.class); // JAVA OBJECT

        //below is the foreach example that can help with your HW
        for (var element : get.data){
            System.out.println(element.email);
        }
//        String json= gson.toJson(get);
//        System.out.println(json); //here I print the serialized Json
    }

    @Test
    public void GetSingleUser(){
        Response response = given().when().get("/users/2").then().statusCode(200).extract().response();
        GetSingleUserDTO dto= gson.fromJson(response.body().asString(), GetSingleUserDTO.class);

        int expectedId = 2;
        String email = "janet.weaver@reqres.in";
        String fName = "Janet";
        String lName = "Weaver";
        String avatar = "https://reqres.in/img/faces/2-image.jpg";
        String url ="https://reqres.in/#support-heading";
        String text = "To keep ReqRes free, contributions towards server costs are appreciated!";

        Assertions.assertEquals(expectedId, dto.data.id);
        Assertions.assertEquals(email, dto.data.email);
        Assertions.assertEquals(fName, dto.data.first_name);
        Assertions.assertEquals(lName, dto.data.last_name);
        Assertions.assertEquals(avatar, dto.data.avatar);
        Assertions.assertEquals(url, dto.support.url);
        Assertions.assertEquals(text, dto.support.text);


    }

    @Test
    public void GetSingleUserNotFound(){
        Response response = given().when().get("/users/23").then().statusCode(404).extract().response();
        Assertions.assertEquals("{}", response.body().asString());
    }

    @Test
    public void GetSingleResource(){
        Response response = given()
                .when()
                .get("/unknown/2")
                .then()
                .statusCode(200)
                .extract().response();

        SingleResourceDTO dto = gson.fromJson(response.body().asString(),SingleResourceDTO.class);

        Assertions.assertEquals(2, dto.data.id);
        Assertions.assertEquals("fuchsia rose", dto.data.name);
        Assertions.assertEquals(2001, dto.data.year);
        Assertions.assertEquals("#C74375", dto.data.color);
        Assertions.assertEquals("17-2031", dto.data.pantone_value);

    }

    @Test
    public void UpdateWithPatch(){
        Response response = given()
                .contentType("application/json")
                .and()
                .body(putRequestBody)
                .when()
                .patch("/users/2")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(putUsername, response.jsonPath().getString("name"));
        Assertions.assertEquals(putJob, response.jsonPath().getString("job"));

        String date= response.jsonPath().getString("updatedAt");
        int lastIndex = date.indexOf("T");
        String assertDate = date.substring(0, lastIndex);
        String dateToday = LocalDate.now().toString();
        Assertions.assertEquals(assertDate, dateToday);
    }


}
