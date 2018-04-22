package edu.ncsu.csc.itrust2.cucumber;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LabProcedureStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait    = new WebDriverWait( driver, 2 );

    /**
     * Admin log in
     */
    @When ( "I login iTrust2 as an admin" )
    public void adminLogin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the lab procedure codes page" )
    public void navigateLabProcedures () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageLabProcedureCodes').click();" );
    }

    @When ( "I navigate to the lab procedures page" )
    public void navigateProcedures () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('procedures').click();" );
    }

    @When ( "I create a lab procedure code" )
    public void createLabProcedureCode () {

    }

    @Then ( "the lab procedure code should be created successfully" )
    public void assertLabProcedure () {

    }

}
