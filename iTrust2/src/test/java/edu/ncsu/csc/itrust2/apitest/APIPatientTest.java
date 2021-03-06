package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        // DomainObject.deleteAll( Patient.class );

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

    @Test
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testRepsPatient () throws Exception {
        Patient antti = Patient.getByName( "antti" );

        // let's do some pr stuff here

        // first try to add Alice as a representative
        mvc.perform( post( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "AliceThirteen" ) ) ).andExpect( status().isOk() );
        antti = Patient.getByName( "antti" );
        final Patient alice = Patient.getByName( "AliceThirteen" );
        assertTrue( antti.getRepresentatives().size() == 1 );
        assertTrue( antti.inRepresentatives( alice ) );
        assertTrue( alice.inRepresentees( antti ) );

        // then let's add an invalid
        mvc.perform( post( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "SlabTheKiller" ) ) ).andExpect( status().isNotFound() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 1 );

        // then we try to add ourselves
        mvc.perform( post( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "antti" ) ) ).andExpect( status().isBadRequest() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 1 );

        // let's do an invalid delete
        mvc.perform( delete( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "SlabTheKiller" ) ) ).andExpect( status().isNotFound() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 1 );
        mvc.perform( delete( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "BobTheFourYearOld" ) ) ).andExpect( status().isBadRequest() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 1 );

        // now let's get our lists
        // don't see any way this can go wrong
        mvc.perform( get( "/api/v1/patients/representatives" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/patients/representees" ) ).andExpect( status().isOk() );

        // add someone else before we delete
        mvc.perform( post( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "BobTheFourYearOld" ) ) ).andExpect( status().isOk() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 2 );

        // and now do our delete
        mvc.perform( delete( "/api/v1/patients/representatives" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "BobTheFourYearOld" ) ) ).andExpect( status().isOk() );
        antti = Patient.getByName( "antti" );
        assertTrue( antti.getRepresentatives().size() == 1 );
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
    @WithMockUser ( username = "hcpguy", roles = { "HCP" } )
    public void testEditRepsHCP () throws Exception {
        HibernateDataGenerator.refreshDB();
        Patient alice = Patient.getByName( "AliceThirteen" );
        Patient tim = Patient.getByName( "TimTheOneYearOld" );
        Patient bob = Patient.getByName( "BobTheFourYearOld" );
        alice.save();
        tim.save();
        bob.save();

        // alice represents tim
        mvc.perform( post( "/api/v1/patients/representees/TimTheOneYearOld" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "AliceThirteen" ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/patients/representees/TimTheOneYearOld" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "AliceThirteen" ) ) ).andExpect( status().isBadRequest() );
        mvc.perform( post( "/api/v1/patients/representees/BuffyTheVampireSlayer" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isNotFound() );
        mvc.perform( post( "/api/v1/patients/representees/TimTheOneYearOld" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "HiroProtagonist" ) ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/v1/patients/representees/TimTheOneYearOld" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "TimTheOneYearOld" ) ) ).andExpect( status().isBadRequest() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        // make sure the relationship goes both ways
        assertTrue( alice.inRepresentees( tim ) );
        assertTrue( tim.inRepresentatives( alice ) );
        // get a false case
        assertFalse( alice.inRepresentatives( tim ) );

        // tim represents bob
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "TimTheOneYearOld" ) ) )
                .andExpect( status().isOk() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );
        // check data is preserved
        assertTrue( alice.inRepresentees( tim ) );
        assertTrue( tim.inRepresentatives( alice ) );
        // check the new one
        assertTrue( tim.inRepresentees( bob ) );
        assertTrue( bob.inRepresentatives( tim ) );

        // alice represents bob
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isOk() );
        // you have to refresh the lists to get their updated info, pretty sure
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );
        // check that alice has 2 or more representees
        assertTrue( alice.getRepresentees().size() == 2 );
        assertTrue( alice.inRepresentees( bob ) );
        assertTrue( alice.inRepresentees( tim ) );
        // check that bob has 2 representatives
        assertTrue( bob.getRepresentatives().size() == 2 );
        assertTrue( bob.inRepresentatives( alice ) );
        assertTrue( bob.inRepresentatives( tim ) );

        // try an invalid couple of calls
        // not found representee
        mvc.perform( post( "/api/v1/patients/representatives/SlabTheKiller" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "AliceThirteen" ) ) ).andExpect( status().isNotFound() );
        // not found representative
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "SlabTheKiller" ) ) )
                .andExpect( status().isNotFound() );
        // can't add yourself
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "BobTheFourYearOld" ) ) )
                .andExpect( status().isBadRequest() );
        // can't add duplicate
        mvc.perform( post( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isBadRequest() );

        // try getting a representatives list
        mvc.perform( get( "/api/v1/patients/representatives/BobTheFourYearOld" ) ).andExpect( status().isOk() );

        // try getting a representees list
        mvc.perform( get( "/api/v1/patients/representees/BobTheFourYearOld" ) ).andExpect( status().isOk() );

        // try an invalid representatives list
        mvc.perform( get( "/api/v1/patients/representatives/SlabTheKiller" ) ).andExpect( status().isNotFound() );

        // try an invalid representees list
        mvc.perform( get( "/api/v1/patients/representees/SlabTheKiller" ) ).andExpect( status().isNotFound() );

        // try doing a delete
        mvc.perform( delete( "/api/v1/patients/representatives/TimTheOneYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isOk() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );
        // check that size updated
        assertTrue( alice.getRepresentees().size() == 1 );
        // check that bob stayed in the list
        assertTrue( alice.inRepresentees( bob ) );
        // check that tim is not in the list
        assertFalse( alice.inRepresentees( tim ) );
        assertFalse( tim.inRepresentatives( alice ) );

        // try some invalid deletes
        // invalid representee
        mvc.perform( delete( "/api/v1/patients/representatives/SlabTheKiller" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isNotFound() );
        // invalid representative
        mvc.perform( delete( "/api/v1/patients/representatives/TimTheOneYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "SlabTheKiller" ) ) )
                .andExpect( status().isNotFound() );
        // delete something that wasn't there
        mvc.perform( delete( "/api/v1/patients/representatives/TimTheOneYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isBadRequest() );
        // delete something that was never there
        mvc.perform( delete( "/api/v1/patients/representatives/TimTheOneYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "BobTheFourYearOld" ) ) )
                .andExpect( status().isBadRequest() );

        // delete the other item
        mvc.perform( delete( "/api/v1/patients/representatives/BobTheFourYearOld" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( "AliceThirteen" ) ) )
                .andExpect( status().isOk() );
        alice = Patient.getByName( "AliceThirteen" );
        tim = Patient.getByName( "TimTheOneYearOld" );
        bob = Patient.getByName( "BobTheFourYearOld" );
        // check the array is empty
        assertTrue( alice.getRepresentees().size() == 0 );
        assertFalse( alice.inRepresentees( bob ) );
    }
}
