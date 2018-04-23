package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;

import java.text.ParseException;
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
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Step Definitions for the Personal Representatives enhancement [UC 16]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class PersonalRepresentativesStepDefs {

    private static final int PAGE_LOAD = 500;
    private WebDriver        driver;
    private final String     baseUrl   = "http://localhost:8080/iTrust2";
    WebDriverWait            wait;

    /**
     * set up the web driver and default wait time
     */
    @Before
    public void setup () {
        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 10 );
        HibernateDataGenerator.generateTestFaculties();
    }

    /**
     * close the web driver to free up processing resources
     */
    @After
    public void tearDown () {
        // driver.quit(); // for chromedriver
        driver.close();
    }

    private void setTextField ( final By byval, final String value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value );
    }

    // ------------------------------------------
    // Scenario: A patient should be able to view
    // and add their own personal representatives
    // ------------------------------------------

    /**
     * Make sure the 3 patients we need are in the database
     *
     * @throws ParseException
     */
    @Given ( "the required patients exist in the database" )
    public void loadRequiredUsers () throws ParseException {

        // make sure the users we need to login exist
        final Patient dbAlice = Patient.getByName( "AliceThirteen" );
        final Patient alice = null == dbAlice ? new Patient() : dbAlice;
        alice.setSelf( User.getByName( "AliceThirteen" ) );
        alice.setEmail( "alice@gmail.com" );
        alice.setAddress1( "123 Alice St." );
        alice.setCity( "Raleigh" );
        alice.setState( State.NC );
        alice.setZip( "12345" );
        alice.setPhone( "123-456-7890" );
        alice.setBloodType( BloodType.BNeg );
        alice.setEthnicity( Ethnicity.Caucasian );
        alice.setGender( Gender.Female );
        alice.save();

        final Patient dbTim = Patient.getByName( "TimTheOneYearOld" );
        final Patient tim = null == dbTim ? new Patient() : dbTim;
        tim.setSelf( User.getByName( "TimTheOneYearOld" ) );
        tim.setEmail( "tim@gmail.com" );
        tim.setAddress1( "123 Tim St." );
        tim.setCity( "Raleigh" );
        tim.setState( State.NC );
        tim.setZip( "12345" );
        tim.setPhone( "123-456-7890" );
        tim.setBloodType( BloodType.BNeg );
        tim.setEthnicity( Ethnicity.Caucasian );
        tim.setGender( Gender.Male );
        tim.save();

        final Patient dbBob = Patient.getByName( "BobTheFourYearOld" );
        final Patient bob = null == dbBob ? new Patient() : dbBob;
        bob.setSelf( User.getByName( "BobTheFourYearOld" ) );
        bob.setEmail( "bob@gmail.com" );
        bob.setAddress1( "123 Bob St." );
        bob.setCity( "Raleigh" );
        bob.setState( State.NC );
        bob.setZip( "12345" );
        bob.setPhone( "123-456-7890" );
        bob.setBloodType( BloodType.BNeg );
        bob.setEthnicity( Ethnicity.Caucasian );
        bob.setGender( Gender.Male );
        bob.save();
    }

    /**
     * User logs into iTrust2
     *
     * @param name
     *            The username of the patient to log in as
     */
    @When ( "I log in to iTrust2 with my username (.+)" )
    public void loginPatient ( final String name ) {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), name );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * HCP assigns a representative
     *
     * @param tive
     *            The representative-to-be
     * @param tee
     *            The representee-to-be
     *
     */
    @When ( "I assign representative (.+) for the patient (.+)" )
    public void hcpAssignTive ( final String tive, final String tee ) {
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=" + tee + "]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=" + tee + "]" ) ).click();
        setTextField( By.name( "representative" ), tive );
        driver.findElement( By.name( "representativeSubmit" ) ).click();
    }

    /**
     * HCP assigns a representee
     *
     * @param tive
     *            The representative-to-be
     * @param tee
     *            The representee-to-be
     *
     */
    @When ( "I assign representee (.+) for the patient (.+)" )
    public void hcpAssignTee ( final String tee, final String tive ) {
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=" + tive + "]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=" + tive + "]" ) ).click();
        setTextField( By.name( "representee" ), tee );
        driver.findElement( By.name( "representeeSubmit" ) ).click();
        Patient.getByName( tive ).save();
        Patient.getByName( tee ).save();
    }

    /**
     * HCP sees that the correct representative has been set
     *
     * @param tive
     *            The representative in question
     * @param tee
     *            The representee in question
     * @throws InterruptedException
     */
    @Then ( "I, as an HCP, see that (.+) now represents (.+)" )
    public void hcpViewRep ( final String tive, final String tee ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( PAGE_LOAD );
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=" + tee + "]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=" + tee + "]" ) ).click();
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "representativeMidCell" ) ) );
        Thread.sleep( PAGE_LOAD );

        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( tive ) ) {
                found = true;
            }
        }
        assert ( found );
        driver.findElement( By.id( "logout" ) ).click();
    }

    /**
     * Patient navigates to their personal reps page
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the personal representatives page" )
    public void goToRepsPage () throws InterruptedException {
        driver.get( baseUrl + "/patient/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );
    }

    /**
     * HCP navigates to the personal reps page
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the HCP personal representatives page" )
    public void goToRepsPageHCP () throws InterruptedException {
        driver.get( baseUrl + "/hcp/viewPersonalRepresentatives" );
        Thread.sleep( PAGE_LOAD );
    }

    /**
     * I assign a personal representative
     *
     * @param name
     *            the user name of the patient to assign as personal
     *            representative
     * @throws Exception
     */
    @When ( "I assign (.+) as a personal representative" )
    public void patientHasReps ( final String name ) throws Exception {
        setTextField( By.id( "addRep" ), name );
        driver.findElement( By.name( "addRepresentativeSubmit" ) ).click();
    }

    /**
     * Patient sees their personal representatives
     *
     * @param name
     *            The name of the person to verify they are a personal
     *            representative
     * @throws InterruptedException
     */
    @Then ( "I should see (.+) as my personal representative" )
    public void viewReps ( final String name ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        Thread.sleep( PAGE_LOAD );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "representativeMidCell" ) ) );
        final WebElement cell = driver.findElement( By.name( "representativeMidCell" ) );
        wait.until( ExpectedConditions.textToBePresentInElement( cell, name ) );
        driver.findElement( By.id( "logout" ) ).click();
    }

    // ------------------------------------------
    // Scenario Outline: A patient should be able
    // to view who they are a representative for
    // ------------------------------------------

    /**
     * The User logs out
     */
    @When ( "I log out" )
    public void logOut () {
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
        Thread.sleep( PAGE_LOAD );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "representeeMidCell" ) ) );
        final WebElement cell = driver.findElement( By.name( "representeeMidCell" ) );
        wait.until( ExpectedConditions.textToBePresentInElement( cell, patient ) );
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
     * @param me
     *            The user logged in
     * @param rep
     *            The personal representative they assigned to themself
     * @throws InterruptedException
     */
    @Then ( "(.+) should not see (.+) as a personal representative" )
    public void notSeeRep ( final String me, final String rep ) throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        // Thread.sleep( DATABASE_UPDATE );
        Patient.getByName( rep ).save();
        Patient.getByName( me ).save();
        assertFalse( Patient.getByName( rep ).inRepresentees( Patient.getByName( me ) ) );
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
}
