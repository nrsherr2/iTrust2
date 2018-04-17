package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.ChromeDriverManager;

/**
 * Step Definitions for the Personal Representatives enhancement [UC 16]
 *
 * @author Justin Schwab (jgschwab)
 *
 */
public class PersonalRepresentativesStepDefs {

    private static final int PAGE_LOAD   = 500;
    private static final int GLOBAL_WAIT = 6000;
    private WebDriver        driver;
    private final String     baseUrl     = "http://localhost:8080/iTrust2";
    WebDriverWait            wait;

    /**
     * set up the web driver and default wait time
     */
    @Before
    public void setup () {
        // driver = new HtmlUnitDriver( true );
        ChromeDriverManager.getInstance().setup();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments( "headless" );
        options.addArguments( "window-size=1200x600" );
        options.addArguments( "blink-settings=imagesEnabled=false" );
        driver = new ChromeDriver( options );
        wait = new WebDriverWait( driver, 25 );
    }

    /**
     * close the web driver to free up processing resources
     */
    @After
    public void tearDown () {
        driver.quit(); // for chromedriver
        // driver.close();
    }

    private void setTextField ( final By byval, final String value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value );
    }

    // ------------------------------------------
    // Scenario: A patient should be able to view
    // their own personal representatives
    // ------------------------------------------

    /**
     * User logs into iTrust2
     *
     * @param name
     *            The username of the patient to log in as
     */
    @Given ( "I log in to iTrust2 with my username (.+)" )
    public void loginPatient ( final String name ) {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), name );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * User navigates to their personal reps page
     *
     * @throws InterruptedException
     */
    @When ( "I navigate to the personal representatives page" )
    public void goToRepsPage () throws InterruptedException {
        driver.get( baseUrl + "/patient/viewPersonalRepresentatives" );
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
        Thread.sleep( GLOBAL_WAIT );
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( name ) ) {
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
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representativeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( rep ) ) {
                found = true;
            }
        }
        assertFalse( found );
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
        final List<WebElement> allMIDCells = driver.findElements( By.name( "representeeMidCell" ) );
        boolean found = false;
        for ( final WebElement w : allMIDCells ) {
            if ( w.getText().contains( representee ) ) {
                found = true;
            }
        }
        assertFalse( found );
        driver.findElement( By.id( "logout" ) ).click();
    }
}
