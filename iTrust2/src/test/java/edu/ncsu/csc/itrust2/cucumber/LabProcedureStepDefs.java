package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * The cucumber step definitions for the lab procedure codes and lab procedures
 * selenium automation
 *
 * @author nlwrenn, amshafe2
 *
 */
public class LabProcedureStepDefs {

    private final WebDriver driver   = new HtmlUnitDriver( true );
    private final String    baseUrl  = "http://localhost:8080/iTrust2";
    WebDriverWait           wait     = new WebDriverWait( driver, 2 );

    /** Constant for the lab procedure code used */
    private final String    LAB_CODE = "10191-2";
    /** Constant for the lab procedure description used */
    private final String    NEW_DESC = "new lpCode";

    private String          search;

    /**
     * Setup
     */
    @Before
    public void setup () {
        final User labtech = new User( "labtech2", "123456", Role.ROLE_LABTECH, 1 );
        labtech.save();
    }

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

    /**
     * Admin log in
     */
    @When ( "I login as a lab tech with a lab procedure" )
    public void labTechLogin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "labtech" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Used to log out of iTrust2
     */
    @When ( "I now logout of itrust" )
    public void logoutLabProc () {
        final WebElement logout = driver.findElement( By.id( "logout" ) );
        logout.click();
    }

    @When ( "I navigate to the lab procedure codes page" )
    public void navigateLabProcedures () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageLabProcedureCodes').click();" );
    }

    /**
     * Used to navigate to the lab procedures page
     */
    @When ( "I navigate to the lab procedures page" )
    public void navigateProcedures () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('procedures').click();" );
    }

    /**
     * Used to create a lab procedure code
     */
    @When ( "I create a lab procedure code" )
    public void createLabProcedureCode () {
        String search = "code";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement codeElement = driver.findElement( By.name( search ) );
        codeElement.clear();
        codeElement.sendKeys( LAB_CODE );

        search = "description";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement descElement = driver.findElement( By.name( search ) );
        descElement.sendKeys( "Office Visit testing Lab Procedure Code" );

        search = "submit";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement sub = driver.findElement( By.name( search ) );
        sub.click();

    }

    /**
     * Makes sure the lab procedure code has been created
     *
     * @throws InterruptedException
     */
    @Then ( "the lab procedure code should be created successfully" )
    public void assertLabProcedureCode () throws InterruptedException {
        Thread.sleep( 3000 );
        boolean found = false;
        final List<LabProcedureCode> list = LabProcedureCode.getAll();
        for ( final LabProcedureCode l : list ) {
            if ( l.getCode().equals( LAB_CODE ) ) {
                found = true;
            }
        }
        assertTrue( found );
    }

    /**
     * Used to delete the lab procedure code
     *
     * @throws InterruptedException
     */
    @Then ( "I delete the lab procedure code" )
    public void deleteLabProcedureCode () throws InterruptedException {
        Thread.sleep( 3000 );
        final List<LabProcedureCode> list = LabProcedureCode.getAll();
        for ( final LabProcedureCode l : list ) {
            if ( l.getCode().equals( LAB_CODE ) ) {
                l.delete();
            }
        }
    }

    /**
     * Used to edit the lab procedure code
     */
    @When ( "I edit the lab procedure code" )
    public void editLabProcedureCode () {
        // TODO when in dev
        String search = "code";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement codeElement = driver.findElement( By.name( search ) );
        codeElement.clear();
        codeElement.sendKeys( LAB_CODE );

        search = "description";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement desc = driver.findElement( By.name( search ) );
        desc.sendKeys( NEW_DESC );

        search = "submit";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement sub = driver.findElement( By.name( search ) );
        sub.click();

    }

    /**
     * Makes sure the lab procedure code was updated
     *
     * @throws InterruptedException
     */
    @Then ( "the lab procedure code should be updated" )
    public void assertLabProcCodeUp () throws InterruptedException {
        Thread.sleep( 3000 );
        boolean found = false;
        final List<LabProcedureCode> list = LabProcedureCode.getAll();
        for ( final LabProcedureCode l : list ) {
            if ( l.getCode().equals( LAB_CODE ) && l.getDescription().equals( NEW_DESC ) ) {
                found = true;
            }
        }
        assertTrue( found );
    }

    /**
     * Makes sure the lab procedure code was deleted
     *
     * @throws InterruptedException
     */
    @Then ( "the lab procedure code should no longer exist" )
    public void assertLabCodeDeleted () throws InterruptedException {
        Thread.sleep( 3000 );
        boolean found = false;
        final List<LabProcedureCode> list = LabProcedureCode.getAll();
        for ( final LabProcedureCode l : list ) {
            if ( l.getCode().equals( LAB_CODE ) ) {
                found = true;
            }
        }
        assertFalse( found );
    }

    /**
     * Used to update the lab procedure
     */
    @When ( "I update the lab procedure" )
    public void updateLabProcedure () {
        search = "selectProcedure";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement codeElement = driver.findElement( By.name( search ) );
        codeElement.click();

        // changet the description
        search = "notes";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement noteElement = driver.findElement( By.name( search ) );
        noteElement.clear();
        noteElement.sendKeys( NEW_DESC );

        search = "procSave";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement submit = driver.findElement( By.name( search ) );
        submit.click();
    }

    /**
     * Makes sure the lab procedure code was updated
     */
    @Then ( "the lab procedure should be updated" )
    public void assertLabCodeUpdated () {
        boolean found = false;
        final List<LabProcedure> list = LabProcedure.getByTech( "labtech" );
        for ( final LabProcedure l : list ) {
            if ( l.getComments().equals( NEW_DESC ) ) {
                found = true;
            }
        }
        assertTrue( found );
    }

    /**
     * Used to reassign a lab procedure
     */
    @When ( "I reassign the lab procedure" )
    public void reassignLabProcedure () {
        search = "selectProcedure";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement codeElement = driver.findElement( By.name( search ) );
        codeElement.click();

        // changet the assignment
        search = "assignTechRow";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement techElement = driver.findElement( By.name( search ) );
        final Select sel = new Select( techElement );
        sel.selectByVisibleText( "labtech2" );

        search = "procSave";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( search ) ) );
        final WebElement submit = driver.findElement( By.name( search ) );
        submit.click();
    }

    /**
     * Makes sure the lab procedure was reassigned
     */
    @Then ( "the lab procedure should be reassigned" )
    public void assertLabCodeReassigned () {
        boolean found = false;
        final List<LabProcedure> list = LabProcedure.getByTech( "labtech2" );
        found = list.size() > 0 ? true : false;
        assertTrue( found );
    }

}
