package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class EmergencyRecordsStepDefs {

    int                  PATIENT_AGE    = 100;
    String               PATIENT_NAME   = "patient";
    String               PATIENT_DOB    = "01/01/2011";
    String               PATIENT_GENDER = "Male";
    String               PATIENT_BLOOD  = "APos";

    private WebDriver    driver;
    private final String baseUrl        = "http://localhost:8080/iTrust2";

    WebDriverWait        wait;

    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );

        final User hcp = new User( "hcp", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP,
                1 );
        hcp.save();

        final User er = new User( "Emergency", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_HCP, 1 );
        er.save();
    }

    @After
    public void tearDown () {
        driver.close();
    }

    @Given ( "There is a patient with the name: (.+)" )
    public void patientExists ( final String name ) throws ParseException {

        /* Create patient record */

        final Patient patient = new Patient();
        patient.setSelf( User.getByName( PATIENT_NAME ) );
        patient.setFirstName( name.split( " " )[0] );
        patient.setLastName( name.split( " " )[1] );
        patient.setEmail( "email@mail.com" );
        patient.setAddress1( "847 address place" );
        patient.setCity( "citytown" );
        patient.setState( State.CA );
        patient.setZip( "91505" );
        patient.setPhone( "123-456-7890" );
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/DD/YYYY", Locale.ENGLISH );

        final Calendar time = Calendar.getInstance();
        time.setTime( sdf.parse( PATIENT_DOB ) );

        patient.setDateOfBirth( time );

        patient.save();

    }

    @When ( "I log into iTrust2 as an ER" )
    public void erLogin () {
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

    @When ( "I navigate to the Emergency Health Records Page" )
    public void navigateToOfficeVisit () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('emergencyHealthRecords').click();" );
    }

    @When ( "I fill in the username of the patient" )
    public void searchName () {

        // Enter the name
        String ename = "searchName";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( ename ) ) );
        final WebElement search = driver.findElement( By.name( ename ) );
        search.clear();
        search.sendKeys( PATIENT_NAME );

        ename = "submit";
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( ename ) ) );
        final WebElement searchButton = driver.findElement( By.name( ename ) );
        searchButton.click();
    }

    @Then ( "the patients medical information is displayed" )
    public void checkRecords () {

        final String[] expected = { PATIENT_NAME, PATIENT_DOB, PATIENT_GENDER, PATIENT_BLOOD };

        for ( int i = 0; i < expected.length; i++ ) {
            assertTrue( driver.getPageSource().contains( expected[i] ) );
        }
    }
}
