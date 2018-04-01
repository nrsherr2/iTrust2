package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.personnel.PasswordChangeForm;
import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIPasswordTest {

    private MockMvc               mvc;
    PasswordEncoder               pe = new BCryptPasswordEncoder();

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    private void changePassword ( final User user, final String password, final String newP ) throws Exception {
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( password );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );
        mvc.perform( post( "/api/v1/changePassword" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) );
    }

    // Save auto-formatter wont let this be a javadoc comment
    // Create user. Starts with password 123456.
    // Changes to 654321.
    // Reset to 98765.
    // Delete user
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testValidPasswordChanges () throws Exception {

        final UserForm patient = new UserForm( "patientPW", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "patientPW" ); // ensure they exist

        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326.201.1@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        assertTrue( pe.matches( "123456", user.getPassword() ) );
        changePassword( user, "123456", "654321" );
        user = User.getByName( "patientPW" ); // reload so changes are visible
        assertFalse( pe.matches( "123456", user.getPassword() ) );
        assertTrue( pe.matches( "654321", user.getPassword() ) );

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();

    }

    /**
     * This tests the RequestReset functionality.
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testRequestReset () throws Exception {

        final UserForm patient = new UserForm( "patientPW", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "patientPW" ); // ensure they exist

        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326.201.1@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        assertTrue( pe.matches( "123456", user.getPassword() ) );
        mvc.perform( post( "/api/v1/requestReset" ).contentType( MediaType.APPLICATION_JSON ).content( "patientPW" ) );
        user = User.getByName( "patientPW" ); // reload so changes are visible

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();

    }

    /**
     * This tests that an unauthenticated user can properly request and finish
     * changing their password
     *
     * @throws Exception
     *             if the request fails
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testValidAPIPasswordChange () throws Exception {

        // Create testing User
        final UserForm patient = new UserForm( "PWPatient1", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "PWPatient1" ); // ensure they exist

        Personnel.deleteAll( Personnel.class );
        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326.201.1@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isOk() );

        assertTrue( pe.matches( "123456", user.getPassword() ) );

        // Start the change password process
        final MvcResult result = mvc.perform( post( "/api/v1/requestPasswordReset" )
                .contentType( MediaType.APPLICATION_JSON ).content( "PWPatient1" ) ).andExpect( status().isOk() )
                .andReturn();

        // Use of Gson to parse a JSON string
        // https://stackoverflow.com/questions/20152710/gson-get-json-value-from-string
        final String jsonString = result.getResponse().getContentAsString();
        final JsonObject jobj = new Gson().fromJson( jsonString, JsonObject.class );
        final long activationId = jobj.get( "message" ).getAsLong();

        // Create PasswordResetForm
        user = User.getByName( "PWPatient1" ); // update the user to get
                                               // the password reset token
                                               // (aka current password)
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( user.getPassword() );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );

        // Finish change password process
        mvc.perform( post( "/api/v1/resetPassword/" + activationId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );

        user = User.getByName( "PWPatient1" ); // reload so changes are
                                               // visible
        assertFalse( pe.matches( "123456", user.getPassword() ) );
        assertTrue( pe.matches( "654321", user.getPassword() ) );

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();

    }

    /**
     * This tests that invalid api requests fail. Invalid passwords and
     * expiration testing handled in unit tests.
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testInvalidPasswordReset () throws Exception {

        // test unknown user
        final String pw = "123456";
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( pw );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );
        mvc.perform( post( "/api/v1/changePassword" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        // test unknown user
        mvc.perform( post( "/api/v1/requestPasswordReset" ).contentType( MediaType.APPLICATION_JSON )
                .content( "neverAUsersName" ) ).andExpect( status().isBadRequest() );

    }

    /**
     * This tests that an authenticated user cannot complete a password change
     * using an invalid PasswordChangeForm.
     *
     * @throws Exception
     */
    @WithMockUser ( username = "PWPatient2", roles = { "USER", "ADMIN" } )
    @Test
    public void testChangePasswordInvalidForm () throws Exception {

        final UserForm patient = new UserForm( "PWPatient2", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "PWPatient2" ); // ensure they exist

        Personnel.deleteAll( Personnel.class );
        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326.201.5@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        // Make invalid PasswordChange
        final String pw = "123456";
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( pw );
        form.setNewPassword( newP );
        form.setNewPassword2( newP + "1" );
        mvc.perform( post( "/api/v1/changePassword" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        user = User.getByName( "PWPatient2" ); // reload so changes are visible
        assertTrue( pe.matches( "123456", user.getPassword() ) );

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();

    }

    /**
     * This tests that a user is unable to finish a password change request
     * using an invalid token
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testFinishChangeInvalidToken () throws Exception {
        final String pw = "123456";
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( pw );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );
        mvc.perform( post( "/api/v1/resetPassword/123456" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * This tests that a user cannot make a password change request using
     * invalid parameters
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testFinishActivationInvalidForm () throws Exception {
        // Create testing User
        final UserForm patient = new UserForm( "PWPatient3", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "PWPatient3" ); // ensure they exist

        Personnel.deleteAll( Personnel.class );
        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326s18.201.5@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isOk() );

        assertTrue( pe.matches( "123456", user.getPassword() ) );

        // Start the change password process
        final MvcResult result = mvc.perform( post( "/api/v1/requestPasswordReset" )
                .contentType( MediaType.APPLICATION_JSON ).content( "PWPatient3" ) ).andExpect( status().isOk() )
                .andReturn();

        // Create Invalid PasswordResetForm
        final String pw = "123456";
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( pw );
        form.setNewPassword( newP );
        form.setNewPassword2( newP + "1" );

        // Use of Gson to parse a JSON string
        // https://stackoverflow.com/questions/20152710/gson-get-json-value-from-string
        final String jsonString = result.getResponse().getContentAsString();
        final JsonObject jobj = new Gson().fromJson( jsonString, JsonObject.class );
        final long activationId = jobj.get( "message" ).getAsLong();

        // Finish change password process
        mvc.perform( post( "/api/v1/resetPassword/" + activationId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        user = User.getByName( "PWPatient3" ); // reload so changes are
                                               // visible

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();
    }

}
