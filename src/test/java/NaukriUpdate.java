import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.io.File;

public class NaukriUpdate {
    public RequestSpecBuilder builder;
    public RequestSpecification requestSpecification;
    public RequestSpecification request;
    String fileKey = "UBu42bJjBKNQ0F";
    String formKey = "F51f8e7e54e205";

    public String login(String username, String password) {
        String data = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        builder = new RequestSpecBuilder();
        requestSpecification = builder.build();
        request = RestAssured.given().relaxedHTTPSValidation();
        builder.addHeader("AppId", "103");
        builder.addHeader("Systemid", "jobseeker");
        builder.setBody(data);
        builder.setContentType("application/json");
        request.spec(requestSpecification);
        ResponseOptions<Response> response = request.post("https://www.naukri.com/central-login-services/v1/login");
        return response.body().jsonPath().get("cookies[0].value").toString();
    }

    public String dashboard(String token) {
        builder = new RequestSpecBuilder();
        requestSpecification = builder.build();
        request = RestAssured.given().relaxedHTTPSValidation();
        builder.addHeader("AppId", "105");
        builder.addHeader("Systemid", "Naukri");
        builder.addHeader("Authorization", "Bearer "+token);
        builder.setContentType("application/json");
        request.spec(requestSpecification);
        ResponseOptions<Response> response = request.get("https://www.naukri.com/cloudgateway-mynaukri/resman-aggregator-services/v0/users/self/dashboard");
        return response.body().jsonPath().get("dashBoard.profileId").toString();
    }

    public void updateHeadline(String token, String id, String headline) {
        String data = "{\"profile\":{\"resumeHeadline\":\""+headline+"\"},\"profileId\":\""+id+"\"}";
        builder = new RequestSpecBuilder();
        requestSpecification = builder.build();
        request = RestAssured.given().relaxedHTTPSValidation();
        builder.addHeader("AppId", "105");
        builder.addHeader("Systemid", "Naukri");
        builder.addHeader("Authorization", "Bearer "+token);
        builder.addHeader("x-http-method-override", "PUT");
        builder.addHeader("x-requested-with", "XMLHttpRequest");
        builder.setContentType("application/json");
        builder.setBody(data);
        request.spec(requestSpecification);
        request.post("https://www.naukri.com/cloudgateway-mynaukri/resman-aggregator-services/v1/users/self/fullprofiles");
    }

    public void uploadResume(String resumeName) {
        File file = new File("./src/main/resources/"+resumeName);
        builder = new RequestSpecBuilder();
        requestSpecification = builder.build();
        request = RestAssured.given().relaxedHTTPSValidation();
        builder.addMultiPart("file", file);
        builder.addMultiPart("formKey", formKey);
        builder.addMultiPart("fileName", resumeName);
        builder.addMultiPart("uploadCallback", "true");
        builder.addMultiPart("fileKey", fileKey);
        builder.addHeader("AppId", "105");
        builder.addHeader("Systemid", "fileupload");
        builder.setContentType("multipart/form-data; boundary=----WebKitFormBoundaryAnunXjVjxKWLOxLw");
        request.spec(requestSpecification);
        request.post("https://filevalidation.naukri.com/file");
    }

    public void submitResume(String token, String id) {
        String data = "{\"textCV\":{\"formKey\":\""+formKey+"\",\"fileKey\":\""+fileKey+"\",\"textCvContent\":null}}";
        builder = new RequestSpecBuilder();
        requestSpecification = builder.build();
        request = RestAssured.given().relaxedHTTPSValidation();
        builder.addHeader("AppId", "105");
        builder.addHeader("Systemid", "105");
        builder.addHeader("Authorization", "Bearer "+token);
        builder.addHeader("x-http-method-override", "PUT");
        builder.addHeader("x-requested-with", "XMLHttpRequest");
        builder.setContentType("application/json");
        builder.setBody(data);
        request.spec(requestSpecification);
        request.post("https://www.naukri.com/cloudgateway-mynaukri/resman-aggregator-services/v0/users/self/profiles/"+id+"/advResume");
    }

    @Test(groups = {"check"})
    public void test_naukri() {
        NaukriUpdate apiBase = new NaukriUpdate();
//        String username = "sureshg8232@gmail.com";
        String username = System.getProperty("username");
//        String password = "ss@230";
        String password = System.getProperty("password");
        String value = apiBase.login(username, password);
        String id = apiBase.dashboard(value);
//        String headline = "QA professional with 6+ years of experience. Expert in Selenium-Java, Playwright-Java, Automation Testing, TestNG, Cucumber, JIRA, API with Rest Assured. Knowledge on Mobile testing using Appium.";
        String headline = System.getProperty("headline");
        apiBase.updateHeadline(value, id, headline);
//        apiBase.uploadResume("SureshG_AutomationTestEngineer_6+Yrs.pdf");
        apiBase.uploadResume(System.getProperty("resume"));
        apiBase.submitResume(value, id);
    }
}