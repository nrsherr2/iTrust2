package edu.ncsu.csc.itrust2.cucumber;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Step Definitions for the Personal Representatives enhancement [UC 16]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class PersonalRepresentativesStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    // -----------------------------------------
    // Scenario: An HCP should be able to view a
    // patient's personal representatives
    // -----------------------------------------

    /**
     * An HCP logs into iTrust2
     */
    @Given ( "I login to iTrust2 as HCP" )
    public void hcpLogin () {
        // TODO
    }

    /**
     * There is a patient with personal representatives
     */
    @Given ( "a patient with personal representatives exists" )
    public void patientHasReps () {
        // TODO
    }

    /**
     * HCP navigates to patient's personal representatives
     */
    @When ( "I navigate to the patient's personal representatives" )
    public void goToPatientReps () {
        // TODO
    }

    /**
     * HCP should see the patient's reps
     */
    @Then ( "I should see the patient's personal representatives" )
    public void canSeePatientReps () {
        // TODO
    }

    // ----------------------------------------
    // Scenario: An HCP should be able to add a
    // personal representative for a patient
    // ----------------------------------------

    /**
     * HCP navigates to personal representatives page
     */
    @Given ( "I navigate to the personal representatives page" )
    public void goToPersonalRepsPage () {
        // TODO
    }

    /**
     * HCP assigns a new personal representative for the patient
     */
    @When ( "I assign a new personal representative for the patient" )
    public void assignPersonalRep () {
        // TODO
    }

    /**
     * The patient's reps will be updated
     */
    @Then ( "the patient's personal representatives should be updated" )
    public void patientRepsUpdated () {
        // TODO
    }

    // ------------------------------------------
    // Scenario: A patient should be able to view
    // their own personal representatives
    // ------------------------------------------

    /**
     * Patient logs into iTrust2
     */
    @Given ( "I login to iTrust2 as Patient" )
    public void loginPatient () {
        // TODO
    }

    /**
     * Patient has at least one personal representative
     */
    @Given ( "I have a personal representative" )
    public void hasReps () {
        // TODO
    }

    /**
     * Patient navigates to their personal reps page
     */
    @When ( "I navigate to my personal representatives page" )
    public void goToRepsPage () {
        // TODO
    }

    /**
     * Patient sees their personal representatives
     */
    @Then ( "I should see my personal representatives" )
    public void viewReps () {
        // TODO
    }

    // ------------------------------------------
    // Scenario Outline: A patient should be able
    // to view who they are a representative for
    // ------------------------------------------

    /**
     * Patient is a personal representative for specified patient
     *
     * @param patient
     *            The patient that I am a representative for
     */
    @Given ( "I am a personal representative for (.+)" )
    public void amRepFor ( final String patient ) {
        // TODO
    }

    /**
     * Patient should see that they are a representative for this person
     *
     * @param patient
     *            The patient that I expect to see that I am a personal
     *            representative for
     *
     */
    @Then ( "I should see that I am a personal representative for (.+)" )
    public void viewAmRepFor ( final String patient ) {
        // TODO
    }

    // -------------------------------------------------
    // Scenario Outline: A patient should be able to add
    // a personal representative for themselves
    // -------------------------------------------------

    /**
     * Logged-in patient assigns the personal representative to themself
     *
     * @param rep
     *            The personal representative they assigned to themself
     */
    @When ( "I assign the personal representative (.+) to myself" )
    public void assignRep ( final String rep ) {
        // TODO
    }

    /**
     * Patient sees that representative in their list of personal
     * representatives
     *
     * @param rep
     *            The personal representative they assigned to themself
     */
    @Then ( "I should see (.+) as one of my personal representatives" )
    public void seeRep ( final String rep ) {
        // TODO
    }

    // ---------------------------------------------
    // Scenario Outline: A patient should be able to
    // remove one of their personal representatives
    // ---------------------------------------------

    /**
     * A patient removes a personal representative
     *
     * @param rep
     *            The personal representative they assigned to themself
     */
    @When ( "I remove my personal representative (.+)" )
    public void removeRep ( final String rep ) {
        // TODO
    }

    /**
     * The patient should not see the person they just removed
     *
     * @param rep
     *            The personal representative they assigned to themself
     */
    @Then ( "I should not see (.+) as one of my personal representatives" )
    public void notSeeRep ( final String rep ) {
        // TODO
    }

    // -------------------------------------------------------
    // Scenario Outline: A patient should be able to undeclare
    // themself as a personal representative
    // -------------------------------------------------------

    /**
     * Patient navigates to page to see that they are a personal representative
     *
     * @param representee
     *            The person they are a representative for
     */
    @Given ( "I navigate to the page to view personal representatives for (.+)" )
    public void goToRepsPage ( final String representee ) {
        // TODO
    }

    /**
     * Patient undeclares themself a representative for this person
     *
     * @param representee
     *            The person they are a representative for
     */
    @When ( "I undeclare myself as a personal representative for (.+)" )
    public void undeclare ( final String representee ) {
        // TODO
    }

    /**
     * Patient should not see themself as a representative for this person
     *
     * @param representee
     *            The person they are a representative for
     */
    @Then ( "I should not see myself as a personal representative for (.+)" )
    public void notSeeRepresentee ( final String representee ) {
        // TODO
    }
}
