package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Step Definitions for the New Users enhancement [UC 1]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class NewUsersStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait;

    /**
     * Setup the driver and generate some users for testing
     */
    @Before
    public void setup () {
        HibernateDataGenerator.generateUsers();
        wait = new WebDriverWait( driver, 20 );
    }

    /**
     * Close the driver after testing
     */
    @After
    public void tearDown () {
        driver.close();
    }

    // --------------------------------------------------------------------------
    // Scenario: Creating a user with the Emergency Responder role should
    // succeed
    // --------------------------------------------------------------------------

    /**
     * The ER user does not exist
     */
    @Given ( "the emergency responder does not exist" )
    public void noER () {
        final List<User> users = User.getUsers();
        for ( final User user : users ) {
            if ( user.getUsername().equals( "emergencyTest" ) ) {
                try {
                    user.delete();
                }
                catch ( final Exception e ) {
                    Assert.fail();
                }
            }
        }
    }

    /**
     * Admin log in
     */
    @When ( "I log in as the admin" )
    public void loginAdmin () {
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
     * Navigate to add user page
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the add user page" )
    public void addUserPage () throws InterruptedException {
        wait.until(
                ExpectedConditions.javaScriptThrowsNoExceptions( "document.getElementById('addnewuser').click();" ) );
        // ( (JavascriptExecutor) driver ).executeScript(
        // "document.getElementById('addnewuser').click();" );
    }

    /**
     * admin fills in values for adding a new ER user
     */
    @When ( "I fill in the values of the Add User form to create a new emergency responder" )
    public void addUserFormER () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( "emergencyTest" );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final Select role = new Select( driver.findElement( By.id( "role" ) ) );
        role.selectByVisibleText( "ROLE_ER" );

        final WebElement enabled = driver.findElement( By.className( "checkbox" ) );
        enabled.click();

        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * ER user is successfully created
     */
    @Then ( "the emergency responder should be created" )
    public void createER () {
        assertTrue( driver.getPageSource().contains( "User added successfully" ) );
    }

    /**
     * the new ER user can successfully log in
     */
    @Then ( "the emergency responder should be able to login" )
    public void loginER () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "emergencyTest" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }

    /**
     * the new ER user can see their landing page
     */
    @Then ( "they should see the emergency responder landing page" )
    public void splashPageER () {
        assertTrue( driver.getPageSource().contains( "Welcome to iTrust2 - Emergency Responder" ) );
    }

    // ---------------------------------------------------------------
    // Scenario: Creating a user with the Lab Tech role should succeed
    // ---------------------------------------------------------------

    /**
     * The ER user does not exist
     */
    @Given ( "the lab tech does not exist" )
    public void noLabTech () {
        final List<User> users = User.getUsers();
        for ( final User user : users ) {
            if ( user.getUsername().equals( "techie69" ) ) {
                try {
                    user.delete();
                }
                catch ( final Exception e ) {
                    Assert.fail();
                }
            }
        }
    }

    /**
     * admin fills in values for adding a new LabTech user
     *
     * @throws InterruptedException
     */
    @When ( "I fill in the values of the Add User form to create a new lab tech" )
    public void addUserFormLabTech () throws InterruptedException {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( "techie69" );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final Select role = new Select( driver.findElement( By.id( "role" ) ) );
        role.selectByVisibleText( "ROLE_LABTECH" );

        final WebElement enabled = driver.findElement( By.className( "checkbox" ) );
        enabled.click();

        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * lab tech user is successfully created
     */
    @Then ( "the lab tech should be created" )
    public void createLabTech () {
        assertTrue( driver.getPageSource().contains( "User added successfully" ) );
    }

    /**
     * the new lab tech user can successfully log in
     */
    @Then ( "the lab tech should be able to login" )
    public void loginLabTech () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "techie69" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }

    /**
     * the new lab tech user can see their landing page
     */
    @Then ( "they should see the lab tech landing page" )
    public void splashPageLabTech () {
        assertTrue( driver.getPageSource().contains( "Welcome to iTrust2 - Lab Tech" ) );
    }

    // ----------------------------------------------------------
    // Scenario Outline: An Emergency Responder should be able to
    // change their password
    // ----------------------------------------------------------

    /**
     * ER logs into iTrust2
     */
    @Given ( "I login to iTrust2 as an emergency responder" )
    public void loginAsER () {
        driver.get( baseUrl );

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "Emergency" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * ER navigates to the change password page
     */
    @Given ( "I navigate to the change password form" )
    public void chgPasswordPageER () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('changePassword').click();" );
    }

    /**
     * ER fills in password change form
     *
     * @param currPassword
     *            The ER's current password
     * @param newPassword
     *            The ER's new password
     */
    @When ( "I fill out the ERs form with current password (.+) and new password (.+)" )
    public void chgPasswordER ( final String currPassword, final String newPassword ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "currentPW" ) ) );

        final WebElement pw = driver.findElement( By.name( "currentPW" ) );
        pw.sendKeys( currPassword );
        final WebElement new1 = driver.findElement( By.name( "newPW" ) );
        new1.sendKeys( newPassword );
        final WebElement new2 = driver.findElement( By.name( "confirmPW" ) );
        new2.sendKeys( newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    /**
     * The ER's password is successfully updated
     */
    @Then ( "My password is updated successfully" )
    public void successfulUpdatePW () {
        try {
            Thread.sleep( 5000 );
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "message" ),
                    "Password changed successfully" ) );
        }
        catch ( final Exception e ) {
            fail( "Message: " + driver.findElement( By.name( "message" ) ).getText() );
        }
    }

    // --------------------------------------------------------------
    // Scenario: A Lab Tech should be able to edit their demographics
    // --------------------------------------------------------------

    /**
     * A lab tech logs in to the system
     */
    @Given ( "I login to iTrust2 as a lab tech" )
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
     * The lab tech navigates to the edit demographics page
     */
    @When ( "I navigate to the edit my demographics page" )
    public void editDemographics () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
    }

    /**
     * The lab tech fills in new demographic info
     */
    @When ( "I fill in new demographics" )
    public void fillDemographics () {
        final WebElement firstName = wait
                .until( ExpectedConditions.visibilityOf( driver.findElement( By.id( "firstName" ) ) ) );
        firstName.clear();
        firstName.sendKeys( "Laboratory" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Technician" );

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "labtechdude69@mail.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Fietsenstalling Jaarbeursplein" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Utrecht" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CA" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "91505" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * The demographics are updated successfully
     *
     * @throws InterruptedException
     */
    @Then ( "The demographics are successfully updated" )
    public void updatedSuccessfully () throws InterruptedException {
        Thread.sleep( 1000 );
        assertTrue( driver.getPageSource().contains( "Your demographics were updated successfully" ) );
    }
}
