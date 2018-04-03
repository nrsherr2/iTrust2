package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Test for API functionality for interacting with Patients
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class APIPatientTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentPatient () throws Exception {
        mvc.perform( get( "/api/v1/patients/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PatientAPI
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPatientAPI () throws Exception {
        // Clear out all patients before running these tests.
        DomainObject.deleteAll( Patient.class );

        final UserForm p = new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "6/15/1977" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        // Editing the patient before they exist should fail
        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isNotFound() );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        // Creating the same patient twice should fail.
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().is4xxClientError() );

        mvc.perform( get( "/api/v1/patients" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/patients/antti" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        patient.setPreferredName( "Antti Walhelm" );

        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Editing with the wrong username should fail.
        mvc.perform( put( "/api/v1/patients/badusername" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isConflict() );

        mvc.perform( delete( "/api/v1/patients" ) );
    }

    /**
     * Test accessing the patient PUT request unauthenticated
     *
     * @throws Exception
     */
    @Test
    public void testPatientUnauthenticated () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "6/15/1977" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Test accessing the patient PUT request as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testPatientAsPatient () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "6/15/1977" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        final Patient antti = new Patient( patient );
        antti.save(); // create the patient if they don't exist already

        // a patient can edit their own info
        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        // but they can't edit someone else's
        patient.setSelf( "patient" );
        mvc.perform( put( "/api/v1/patients/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Test the API functionality for editing a patient's personal
     * representatives and representees
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testEditReps () throws Exception {
        HibernateDataGenerator.refreshDB();
        Patient alice = Patient.getByName( "AliceThirteen" );
        Patient tim = Patient.getByName( "TimTheOneYearOld" );
        Patient bob = Patient.getByName( "BobTheFourYearOld" );
        alice.save();
        tim.save();
        bob.save();

        // alice represents tim
        mvc.perform( post( "/api/v1/patients/representatives/TimTheOneYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isOk() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );

        // TODO add some more POST requests if necessary

        System.out.println( "**************BOB***************\n" );
        for ( int i = 0; i < bob.getRepresentatives().size(); i++ ) {
            System.out.println( bob.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************TIM***************\n" );
        for ( int i = 0; i < tim.getRepresentatives().size(); i++ ) {
            System.out.println( tim.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************ALICE***************\n" );
        for ( int i = 0; i < alice.getRepresentatives().size(); i++ ) {
            System.out.println( alice.getRepresentatives().toArray()[i].toString() );
        }
        Thread.sleep( 5000 );

        // bob represents alice
        /*
         * mvc.perform( post( "/api/v1/patients/representatives/AliceThirteen"
         * ).contentType( MediaType.APPLICATION_JSON ) .content(
         * TestUtils.asJsonString( "BobTheFourYearOld" ) ) ).andExpect(
         * status().isOk() );
         */

        // tim represents bob
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "TimTheOneYearOld" ) ) )
                .andExpect( status().isOk() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );

        // TODO add some more POST requests if necessary

        System.out.println( "**************BOB***************\n" );
        for ( int i = 0; i < bob.getRepresentatives().size(); i++ ) {
            System.out.println( bob.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************TIM***************\n" );
        for ( int i = 0; i < tim.getRepresentatives().size(); i++ ) {
            System.out.println( tim.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************ALICE***************\n" );
        for ( int i = 0; i < alice.getRepresentatives().size(); i++ ) {
            System.out.println( alice.getRepresentatives().toArray()[i].toString() );
        }
        Thread.sleep( 5000 );

        // alice represents bob
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isOk() );
        Thread.sleep( 5000 );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );

        // TODO add some more POST requests if necessary

        System.out.println( "**************BOB***************\n" );
        for ( int i = 0; i < bob.getRepresentatives().size(); i++ ) {
            System.out.println( bob.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************TIM***************\n" );
        for ( int i = 0; i < tim.getRepresentatives().size(); i++ ) {
            System.out.println( tim.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************ALICE***************\n" );
        for ( int i = 0; i < alice.getRepresentatives().size(); i++ ) {
            System.out.println( alice.getRepresentatives().toArray()[i].toString() );
        }
        Thread.sleep( 5000 );
        System.out.println( "**************done with post***********************" );
        Thread.sleep( 10000 );

        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );

        // TODO add some more POST requests if necessary

        System.out.println( "**************BOB***************\n" );
        for ( int i = 0; i < bob.getRepresentatives().size(); i++ ) {
            System.out.println( bob.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************TIM***************\n" );
        for ( int i = 0; i < tim.getRepresentatives().size(); i++ ) {
            System.out.println( tim.getRepresentatives().toArray()[i].toString() );
        }
        System.out.println( "**************ALICE***************\n" );
        for ( int i = 0; i < alice.getRepresentatives().size(); i++ ) {
            System.out.println( alice.getRepresentatives().toArray()[i].toString() );
        }
        Thread.sleep( 50000 );
        assertTrue( bob.getRepresentatives().contains( alice ) );
        assertFalse( alice.getRepresentatives().contains( bob ) );
        assertTrue( tim.getRepresentatives().contains( alice ) );
        assertTrue( alice.getRepresentees().contains( bob ) );

        mvc.perform( get( "/api/v1/patients/representatives/BobTheFourYearOld" ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/patients/representees/BobTheFourYearOld" ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( tim ) ) )
                .andExpect( status().isOk() );

        assertFalse( bob.getRepresentatives().contains( tim ) );
    }
}
