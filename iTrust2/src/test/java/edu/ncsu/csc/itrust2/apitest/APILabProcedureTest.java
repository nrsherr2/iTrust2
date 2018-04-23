package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import edu.ncsu.csc.itrust2.forms.admin.LabProcedureCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabProcedureForm;
import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.LabProcedurePriority;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;
import edu.ncsu.csc.itrust2.models.persistent.LabTech;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Class that tests the use of lab procedures within the API.
 * 
 * @author Nick Sherrill
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILabProcedureTest {
    private MockMvc               mvc;
    @Autowired
    private WebApplicationContext context;
    private static String         BASE_PATH = "/api/v1/";

    /**
     * sets up the tests
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * make sure we can edit stuff only if we have permission
     */
    public void testNoPermission () {
        try {
            mvc.perform( delete( BASE_PATH + "labcodes/-1" ) ).andExpect( status().isBadRequest() );
            fail( "you shouldn't be authenticated" );
        }
        catch ( Exception e ) {
            // we're good
        }

    }

    /**
     * let's go through some of the major functionality of working with the lab
     * codes in the API.
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin2", roles = { "USER", "ADMIN" } )
    public void testLabCodes () throws UnsupportedEncodingException, Exception {
        HibernateDataGenerator.refreshDB();
        LabProcedureCodeForm form = new LabProcedureCodeForm();
        form.setCode( "12345-6" );
        form.setDescription( "help" );

        // get an invalid code
        mvc.perform( get( BASE_PATH + "labcodes/-1" ) ).andExpect( status().isNotFound() );

        // delete an invalid code
        mvc.perform( delete( BASE_PATH + "labcodes/-1" ) ).andExpect( status().isBadRequest() );

        final String content1 = mvc
                .perform( post( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Gson gson = new GsonBuilder().create();
        final LabProcedureCode code = gson.fromJson( content1, LabProcedureCode.class );
        mvc.perform( get( BASE_PATH + "labcodes" ) ).andExpect( status().isOk() );
        mvc.perform( get( BASE_PATH + "labcodes/" + code.getId() ) ).andExpect( status().isOk() );
        // try to edit a invalid code
        mvc.perform( put( BASE_PATH + "labcodes/" + Integer.toString( -1 ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isNotFound() );

        // now update a code to be invalid
        form.setCode( "123456" );
        mvc.perform( put( BASE_PATH + "labcodes/" + code.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        // now do a valid update
        form.setCode( "12345-6" );
        form.setDescription( "Senpai notice me!" );
        mvc.perform( put( BASE_PATH + "labcodes/" + code.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );

        mvc.perform( get( BASE_PATH + "labcodes" ) ).andExpect( status().isOk() );
        mvc.perform( get( BASE_PATH + "labcodes/" + code.getId() ) ).andExpect( status().isOk() );

        // now let's delete it
        mvc.perform( delete( BASE_PATH + "labcodes/" + code.getId() ) ).andExpect( status().isOk() );

    }

    @Test
    @WithMockUser ( username = "labtech", roles = "LABTECH" )
    public void testProcedures () throws Exception {

        // only way to create a procedure is through an office visit
        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "AliceThirteen" ) );
        visit.setDate( Calendar.getInstance() );
        visit.save();

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        visit.save();

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        // add in a lab procedure to this visit to create it
        final LabProcedureCode lpc = new LabProcedureCode();
        lpc.setCode( "12345-6" );
        lpc.setDescription( "Listen to 24/7 lo-fi hip hop study beats" );
        lpc.save();
        final Calendar end = Calendar.getInstance();
        end.add( Calendar.DAY_OF_WEEK, 10 );

        final LabProcedureForm lpf = new LabProcedureForm();
        lpf.setAssignedTech( "labtech" );
        lpf.setLabProcedureCode( lpc );
        lpf.setLpp( LabProcedurePriority.PRIORITY_2 );
        lpf.setComments( "Post in chat" );
        lpf.setOv( visit );
        lpf.setStatus( "NOT_STARTED" );
        final LabProcedure proc = new LabProcedure( lpf );
        proc.save();

        pres.setEndDate( end );
        pres.setPatient( User.getByName( "AliceThirteen" ) );
        pres.setStartDate( Calendar.getInstance() );
        pres.setRenewals( 5 );

        visit.setProcedures( Collections.singletonList( proc ) );

        visit.save();

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();

        // start by getting the list of lab techs
        mvc.perform( get( BASE_PATH + "labtechs" ) ).andExpect( status().isOk() );

        // now let's get a list of everything that's assigned to us
        mvc.perform( get( BASE_PATH + "labprocedures" ) ).andExpect( status().isOk() );
        // now let's edit a labprocedure
        assertNotNull( proc.getId() );
        lpf.setId( proc.getId().toString() );
        lpf.setStatus( "COMPLETE" );
        // start by try to edit something that doesn't exist
        mvc.perform( put( BASE_PATH + "labprocedures/-1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf ) ) ).andExpect( status().isNotFound() );

        // then do it for real
        mvc.perform( put( BASE_PATH + "labprocedures/" + proc.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf ) ) ).andExpect( status().isOk() );

        // TODO: add this back in when you merge and the duplication issue is
        // fixed
        // now let's make a conflict
        // add in a lab procedure to this visit to create it
        // final LabProcedureCode lpc2 = new LabProcedureCode();
        // lpc2.setCode( "12345-7" );
        // lpc2.setDescription( "Praise Dr. Jenkins" );
        // lpc2.save();
        // final LabProcedureForm lpf2 = new LabProcedureForm();
        // lpf2.setAssignedTech( "labtech" );
        // lpf2.setLabProcedureCode( lpc2 );
        // lpf2.setLpp( LabProcedurePriority.PRIORITY_2 );
        // lpf2.setComments( "Jenkins is love. Jenkins is life." );
        // lpf2.setOv( visit );
        // lpf2.setStatus( "NOT_STARTED" );
        // final LabProcedure proc2 = new LabProcedure( lpf2 );
        // proc2.save();
        // ArrayList<LabProcedure> list = new ArrayList<LabProcedure>();
        // list.add( proc );
        // list.add( proc2 );
        // visit.setProcedures( list );
        // visit.save();
        // //now let's do the put
        // mvc.perform( put( BASE_PATH + "labprocedures/" + proc.getId()
        // ).contentType( MediaType.APPLICATION_JSON )
        // .content( TestUtils.asJsonString( lpf2 ) ) ).andExpect(
        // status().isOk() );
    }
}
