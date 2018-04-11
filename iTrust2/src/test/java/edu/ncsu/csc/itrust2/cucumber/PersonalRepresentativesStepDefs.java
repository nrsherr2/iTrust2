package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;

import java.util.HashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Patient;

/**
 * Step Definitions for the Personal Representatives enhancement [UC 16]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class PersonalRepresentativesStepDefs {

    private WebDriver    driver;
    private final String baseUrl = "http://localhost:8080/iTrust2";

    WebDriverWait        wait;

    /**
     * set up the web driver and default wait time
     */
    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 10 );
    }

    /**
     * close the web driver to free up processing resources
     */
    @After
    public void tearDown () {
        driver.quit();
    }

    private void setTextField ( final By byval, final String value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value );
    }

    // -----------------------------------------
    // Scenario: An HCP should be able to view a
    // patient's personal representatives
    // -----------------------------------------

    /**
     * An HCP logs into iTrust2
     */
    @Given ( "I login to iTrust2 as HCP" )
    public void hcpLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "hcp" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * There is a patient with personal representatives
     *
     * @throws Exception
     */
    @Given ( "a patient with personal representatives exists" )
    public void patientHasReps () throws Exception {
        // bob represents alice
        final Patient alice = Patient.getByName( "AliceThirteen" );
        if ( alice == null ) {
            throw new Exception( "cannot find alice" );
        }
        final Patient bob = Patient.getByName( "BobTheFourYearOld" );
        if ( bob == null ) {
            throw new Exception( "cannot find bob" );
        }
        // now add the rep
        try {
            final HashSet<Patient> newList = new HashSet<Patient>();
            newList.addAll( alice.getRepresentatives() );
            newList.add( bob );
            alice.setRepresenatives( newList );
            alice.save();
        }
        catch ( final Exception e ) {
            System.out.println( e.getMessage() );
        }
    }

    /**
     * HCP navigates to patient's personal representatives
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the patient's personal representatives" )
    public void goToPatientReps () throws InterruptedException {
        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('viewPersonalRepresentatives').click();" );
        Thread.sleep( 500 );
    }

    /**
     * HCP should see the patient's reps
     */
    @Then ( "I should see the patient's personal representatives" )
    public void canSeePatientReps () {
        setTextField( By.name( "search" ), "Alice" );
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ).click();
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "representativeMidCell" ),
                "BobTheFourYearOld" ) );
    }

    // ----------------------------------------
    // Scenario: An HCP should be able to add a
    // personal representative for a patient
    // ----------------------------------------

    /**
     * HCP navigates to personal representatives page
     *
     * @throws InterruptedException
     */
    @Given ( "I navigate to the personal representatives page" )
    public void goToPersonalRepsPage () throws InterruptedException {
        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('viewPersonalRepresentatives').click();" );
        Thread.sleep( 1000 );
    }

    /**
     * HCP assigns a new personal representative for the patient
     */
    @When ( "I assign a new personal representative for the patient" )
    public void assignPersonalRep () {
        setTextField( By.name( "search" ), "Bob" );
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=BobTheFourYearOld]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=BobTheFourYearOld]" ) ).click();
        setTextField( By.name( "representative" ), "TimTheOneYearOld" );
        driver.findElement( By.name( "representativeSubmit" ) ).click();
    }

    /**
     * The patient's reps will be updated
     *
     * @throws InterruptedException
     */
    @Then ( "the patient's personal representatives should be updated" )
    public void patientRepsUpdated () throws InterruptedException {
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "representativeMidCell" ),
                "TimTheOneYearOld" ) );
    }

    // ------------------------------------------
    // Scenario: A patient should be able to view
    // their own personal representatives
    // ------------------------------------------

    /**
     * Alice logs into iTrust2
     */
    @Given ( "I login to iTrust2 as Alice" )
    public void loginPatient () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "AliceThirteen" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * Alice has at least one personal representative (Tim)
     *
     * @throws InterruptedException
     */
    @Given ( "I have a personal representative" )
    public void hasReps () throws InterruptedException {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "hcp" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('viewPersonalRepresentatives').click();" );
        Thread.sleep( 500 );

        setTextField( By.name( "search" ), "Alice" );
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ).click();
        setTextField( By.name( "representative" ), "TimTheOneYearOld" );
        driver.findElement( By.name( "representativeSubmit" ) ).click();
        driver.findElement( By.id( "logout" ) ).click();
    }

    /**
     * User navigates to their personal reps page
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to my personal representatives page" )
    public void goToRepsPage () throws InterruptedException {
        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('viewPersonalRepresentatives').click();" );
        Thread.sleep( 1000 );
    }

    /**
     * Patient sees their personal representatives (just Tim)
     */
    @Then ( "I should see my personal representatives" )
    public void viewReps () {
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "representativeMidCell" ),
                "TimTheOneYearOld" ) );
        driver.findElement( By.id( "logout" ) ).click();
    }

    // ------------------------------------------
    // Scenario Outline: A patient should be able
    // to view who they are a representative for
    // ------------------------------------------

    /**
     * Alice is a personal representative for specified patient
     *
     * @param patient
     *            The MID of the patient that I am a representative for
     * @throws InterruptedException
     */
    @Given ( "I am a personal representative for (.+)" )
    public void amRepFor ( final String patient ) throws InterruptedException {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "hcp" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('viewPersonalRepresentatives').click();" );
        Thread.sleep( 500 );

        setTextField( By.name( "search" ), patient );
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=" + patient + "]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=" + patient + "]" ) ).click();
        setTextField( By.name( "representative" ), "AliceThirteen" );
        driver.findElement( By.name( "representativeSubmit" ) ).click();
        driver.findElement( By.id( "logout" ) ).click();
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
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "representeeMidCell" ), patient ) );
        driver.findElement( By.id( "logout" ) ).click();
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
     * @throws InterruptedException
     */
    @When ( "I assign the personal representative (.+) to myself" )
    public void assignRep ( final String rep ) throws InterruptedException {
        Thread.sleep( 1000 );
        setTextField( By.id( "addRep" ), rep );
        driver.findElement( By.name( "addRepresentativeSubmit" ) ).click();
    }

    /**
     * Patient sees that representative in their list of personal
     * representatives
     *
     * @param rep
     *            The personal representative they assigned to themself
     * @throws InterruptedException
     */
    @Then ( "I should see (.+) as one of my personal representatives" )
    public void seeRep ( final String rep ) throws InterruptedException {
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "representativeMidCell" ), rep ) );
        driver.findElement( By.id( "logout" ) ).click();
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
        setTextField( By.name( "deleteRepresentative" ), rep );
        driver.findElement( By.name( "deleteRepresentativeSubmit" ) ).click();

    }

    /**
     * The patient should not see the person they just removed
     *
     * @param rep
     *            The personal representative they assigned to themself
     */
    @Then ( "I should not see (.+) as one of my personal representatives" )
    public void notSeeRep ( final String rep ) {
        try {
            // if there exists a representative that's not the one we're
            // confirming isn't there, this will pass
            assertFalse( rep.equals( driver.findElement( By.name( "representativeMidCell" ) ).getText() ) );
        }
        catch ( final Exception e ) {
            // the element wasn't found at all, which means there were no
            // representatives at all in the table
        }
        driver.findElement( By.id( "logout" ) ).click();
    }

    // -------------------------------------------------------
    // Scenario Outline: A patient should be able to undeclare
    // themself as a personal representative
    // -------------------------------------------------------

    /**
     * Patient undeclares themself a representative for this person
     *
     * @param representee
     *            The person they are a representative for
     */
    @When ( "I undeclare myself as a personal representative for (.+)" )
    public void undeclare ( final String representee ) {
        setTextField( By.name( "representee" ), representee );
        driver.findElement( By.name( "deleteRepresenteeSubmit" ) ).click();
    }

    /**
     * Patient should not see themself as a representative for this person
     *
     * @param representee
     *            The person they are a representative for
     */
    @Then ( "I should not see myself as a personal representative for (.+)" )
    public void notSeeRepresentee ( final String representee ) {
        try {
            // if there exists a representee that's not the one we're
            // confirming isn't there, this will pass
            assertFalse( representee.equals( driver.findElement( By.name( "representeeMidCell" ) ).getText() ) );
        }
        catch ( final Exception e ) {
            // the element wasn't found at all, which means there were no
            // representees at all in the table
        }
        driver.findElement( By.id( "logout" ) ).click();
    }
}
