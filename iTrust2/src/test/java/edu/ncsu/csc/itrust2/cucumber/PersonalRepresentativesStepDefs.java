package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
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

/**
 * Step Definitions for the Personal Representatives enhancement [UC 16]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class PersonalRepresentativesStepDefs {

    private static final int PAGE_LOAD   = 500;
    private static final int GLOBAL_WAIT = 3000;
    private final WebDriver  driver      = new HtmlUnitDriver( true );
    private final String     baseUrl     = "http://localhost:8080/iTrust2";

    WebDriverWait            wait;

    /**
     * set up the web driver and default wait time
     */
    @Before
    public void setup () {
        wait = new WebDriverWait( driver, 20 );
    }

    /**
     * close the web driver to free up processing resources
     */
    @After
    public void tearDown () {
        // driver.quit();
        driver.close();
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
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "hcp" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );

        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ).click();
        setTextField( By.name( "representative" ), "BobTheFourYearOld" );
        driver.findElement( By.name( "representativeSubmit" ) ).click();

        driver.findElement( By.id( "logout" ) ).click();
    }

    /**
     * HCP navigates to patient's personal representatives
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the patient's personal representatives" )
    public void goToPatientReps () throws InterruptedException {
        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );
    }

    /**
     * HCP should see the patient's reps
     *
     * @throws InterruptedException
     */
    @Then ( "I should see the patient's personal representatives" )
    public void canSeePatientReps () throws InterruptedException {
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=AliceThirteen]" ) ).click();
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( "BobTheFourYearOld" ) ) {
                found = true;
            }
        }
        assertTrue( found );

        driver.findElement( By.id( "logout" ) ).click();
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
        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );
    }

    /**
     * HCP assigns a new personal representative for the patient
     */
    @When ( "I assign a new personal representative for the patient" )
    public void assignPersonalRep () {
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
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( "TimTheOneYearOld" ) ) {
                found = true;
            }
        }
        assertTrue( found );
        driver.findElement( By.id( "logout" ) ).click();
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

        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives" );

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
        driver.get( baseUrl + "/patient/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );
    }

    /**
     * Patient sees their personal representatives (just Tim)
     *
     * @throws InterruptedException
     */
    @Then ( "I should see my personal representatives" )
    public void viewReps () throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( "TimTheOneYearOld" ) ) {
                found = true;
            }
        }
        assertTrue( found );
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

        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives/" );
        Thread.sleep( PAGE_LOAD );

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
     * @throws InterruptedException
     *
     */
    @Then ( "I should see that I am a personal representative for (.+)" )
    public void viewAmRepFor ( final String patient ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representeeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( patient ) ) {
                found = true;
            }
        }
        assertTrue( found );
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
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "footer" ) ) );
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
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( rep ) ) {
                found = true;
            }
        }
        assertTrue( found );
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
        final WebElement btn = driver.findElement( By.name( "deleteRepresentativeSubmit" ) );
        btn.click();

    }

    /**
     * The patient should not see the person they just removed
     *
     * @param rep
     *            The personal representative they assigned to themself
     * @throws InterruptedException
     */
    @Then ( "I should not see (.+) as one of my personal representatives" )
    public void notSeeRep ( final String rep ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );

        assertFalse( driver.getPageSource().contains( rep ) );
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
        final WebElement btn = driver.findElement( By.name( "deleteRepresenteeSubmit" ) );
        btn.click();
    }

    /**
     * Patient should not see themself as a representative for this person
     *
     * @param representee
     *            The person they are a representative for
     * @throws InterruptedException
     */
    @Then ( "I should not see myself as a personal representative for (.+)" )
    public void notSeeRepresentee ( final String representee ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( GLOBAL_WAIT );
        assertFalse( driver.getPageSource().contains( representee ) );
        driver.findElement( By.id( "logout" ) ).click();
    }
}
