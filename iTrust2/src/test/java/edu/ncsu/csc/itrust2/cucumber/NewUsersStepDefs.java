package edu.ncsu.csc.itrust2.cucumber;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Step Definitions for the New Users enhancement [UC 1]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class NewUsersStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    // --------------------------------------------------------------------------
    // Scenario: Creating a user with the Emergency Responder role should
    // succeed
    // --------------------------------------------------------------------------

    /**
     * The ER user does not exist
     */
    @Given ( "the emergency responder does not exist" )
    public void noER () {
        // TODO
    }

    /**
     * admin fills in values for adding a new ER user
     */
    @When ( "I fill in the values in the Add User form to create a new emergency responder" )
    public void addUserFormER () {
        // TODO
    }

    /**
     * ER user is successfully created
     */
    @Then ( "the emergency responder should be created" )
    public void createER () {
        // TODO
    }

    /**
     * the new ER user can successfully log in
     */
    @Then ( "the emergency responder should be able to login" )
    public void loginER () {
        // TODO
    }

    /**
     * the new ER user can see their landing page
     */
    @Then ( "they should see the emergency responder landing page" )
    public void splashPageER () {
        // TODO
    }

    // ---------------------------------------------------------------
    // Scenario: Creating a user with the Lab Tech role should succeed
    // ---------------------------------------------------------------

    /**
     * The ER user does not exist
     */
    @Given ( "the lab tech does not exist" )
    public void noLabTech () {
        // TODO
    }

    /**
     * admin fills in values for adding a new LabTech user
     */
    @When ( "I fill in the values in the Add User form to create a new lab tech" )
    public void addUserFormLabTech () {
        // TODO
    }

    /**
     * lab tech user is successfully created
     */
    @Then ( "the lab tech should be created" )
    public void createLabTech () {
        // TODO
    }

    /**
     * the new lab tech user can successfully log in
     */
    @Then ( "the lab tech should be able to login" )
    public void loginLabTech () {
        // TODO
    }

    /**
     * the new lab tech user can see their landing page
     */
    @Then ( "they should see the lab tech landing page" )
    public void splashPageLabTech () {
        // TODO
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
        // TODO basically a duplicate of "the emergency responder should be
        // able to login"
    }

    /**
     * ER navigates to the change password page
     */
    @Given ( "I navigate to the change password form" )
    public void chgPasswordPageER () {
        // TODO
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
        // TODO
    }

    /**
     * The ER's password is successfully updated
     */
    @Then ( "My password is updated successfully" )
    public void successfulUpdatePW () {
        // TODO
    }

    // --------------------------------------------------------------
    // Scenario: A Lab Tech should be able to edit their demographics
    // --------------------------------------------------------------

    /**
     * A lab tech logs in to the system
     */
    @Given ( "I login to iTrust2 as a lab tech" )
    public void labTechLogin () {
        // TODO
    }
}
