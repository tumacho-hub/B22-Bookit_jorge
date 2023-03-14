package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {
        /*
        On the request below we are sending information to API-POSTMAN to be able to retrieve our token
        but the since this is something repetitive we are creating a new method under Utilities package
        so instead of writing the whole lines over and over we can call our method name with our ready request

         Response response = given().
                accept(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .get(Environment.BASE_URL + "/sign")
                .then().log().all().extract().response();

        String token = response.path("accessToken");

        then to be able to use these variables we need to make them INSTANCE VARIABLES to have access to in the entire
        class
         */

        token = BookItApiUtil.generateToken(email,password);
        emailGlobal = email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        System.out.println("token = " + token);

        /*
        send a GET request "/api/users/me" endpoint to get current user info
        The code below is sending the request to our end point which already includes the
         .header("Authorization", token) with "Authorization and our token
         just another simple request with headers
         */

         response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when()
                .get(Environment.BASE_URL + "/api/users/me");

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        /*
        verify status code matches with the feature file expected status code
        in this step definition all we are doing is verification
         */
        Assert.assertEquals(statusCode,response.statusCode());

    }

    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        System.out.println("we will compare database and api in this step");

        /*
        //Task: get information from database
        In order to connect and get the information from dataBase  we need connection to JDBC<-- java database connection
        We are implementing this connection on our hooks class with the method below
        @Before("@db") <-- our annotation that will run before all with the
        "@db" tag indicating is specifically for database
	    public void dbHook() {
		System.out.println("creating database connection");
		DBUtils.createConnection(); <-- this a method that is already implemented in our DBUtils class
		After establishing our connection we need our query that will run inside the JDBC
         */
        //Query that we need to send to our JDBC
        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";
        /*
        emailGlobal <-- this instance variable comes from our first step definition and to be able to reuse it we are
        declaring it as Instance variable that way we can get access through the whole class
         */

        /*
        Since our query is ready
        Now all we have to do is to send information to get one row of information from JDBC
        IN this case we are sending as MAP
        Map<String,Object> dbMap = DBUtils.getRowMap(query); <-- this line is doing two action
        Map<String,Object> dbMap <-- out map that we are creating to send the information to our JDBC
        DBUtils.getRowMap <-- this is a method that comes from DBUtils and all is doing
        retrieving one row

         */



        /*
        Now how do we get the information from POSTMAN-API?
        This step will is already completed above all we have to do is implement it
        in these specific step definition
         response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when()
                .get(Environment.BASE_URL + "/api/users/me");
         Now we will use JsonPath to get the information from api
         */

        Map<String,Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);
        //save db info into variables
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");



        //getting information from api using JsonPath
        JsonPath jsonPath = response.jsonPath();
        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //Now we are doing our assertions compare database vs api
        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() {
        /*
        For this step definition we are using the same code above for fourth first step
        because is the same logic
        we are getting information from database
        we are getting information from api
         */

        //get information from database
        //connection is from hooks and it will be ready
        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";

        Map<String,Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);
        //save db info into variables
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();
        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        /*
        Below we have code tha makes the verification from UI
         SelfPage selfPage = new SelfPage(); <-- object from our pages class
        String actualUIName = selfPage.name.getText(); <--locators coming from our pages classes
        String actualUIRole = selfPage.role.getText(); <--locators coming from our pages classes

        System.out.println("actualUIName = " + actualUIName); <-- our assertions
        System.out.println("actualUIRole = " + actualUIRole);
         */

        //get information from UI
        SelfPage selfPage = new SelfPage();
        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println("actualUIName = " + actualUIName);
        System.out.println("actualUIRole = " + actualUIRole);

         /*
         UI VS DB
         our final step is to compare UI vs DB
         String expectedFullName = expectedFirstName+" "+expectedLastName;
         expectedFirstName <-- this value comes from dataBase above code on previous steps definitions
          */
        String expectedFullName = expectedFirstName+" "+expectedLastName;
        //verify ui fullname vs db fullname
        Assert.assertEquals(expectedFullName,actualUIName);
        Assert.assertEquals(expectedRole,actualUIRole);

        /*
        Last step is to verify UI VS API
        so the code below is verifying these two points
         */

        //UI vs API
        //Create a fullname for api
        String actualFullName = actualFirstName+" "+actualLastName;
        Assert.assertEquals(actualFullName,actualUIName);
        Assert.assertEquals(actualRole,actualUIRole);

    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String,String> studentInfo) {
        /*
        for this scenario we have the following string arguments
        (String path, Map<String,String> studentInfo)
        String path, <-- this one comes from our scenario
         When I send POST request to "/api/students/student" endpoint with following information <-- this is the line that creates
         the specific --> String path
         Map<String,String> studentInfo <-- the rest of the line is base on the following info
      | first-name      | harold              |
      | last-name       | finch               |
      | email           | harold10@gmail.com  |
      | password        | abc123              |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                   |
      | team-name       | Nukes               |
         */



        //why we prefer to get information as a map from feature file ?
        //bc we have queryParams method that takes map and pass to url as query key&value structure
        System.out.println("studentInfo = " + studentInfo);

        //assign email and password value to these variables so that we can use them later for deleting
        studentEmail = studentInfo.get("email");
        studentPassword = studentInfo.get("password");

        response = given().accept(ContentType.JSON)
                .queryParams(studentInfo)
                .and().header("Authorization",token)
                .log().all()
                .when()
                .post(Environment.BASE_URL + path)
        .then().log().all().extract().response();        ;


    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        BookItApiUtil.deleteStudent(studentEmail,studentPassword);
    }


    @Given("I logged Bookit api as {string}")
    public void iLoggedBookitApiAs(String role) {
       token= BookItApiUtil.getTokenByRole(role);
    }
}
