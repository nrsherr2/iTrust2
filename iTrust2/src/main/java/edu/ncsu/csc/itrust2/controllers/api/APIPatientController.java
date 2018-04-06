package edu.ncsu.csc.itrust2.controllers.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Patient model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientController extends APIController {

    /**
     * Retrieves and returns a list of all Patients stored in the system
     *
     * @return list of patients
     */
    @GetMapping ( BASE_PATH + "/patients" )
    public List<Patient> getPatients () {
        return Patient.getPatients();
    }

    /**
     * If you are logged in as a patient, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The patient object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/patient" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPatient () {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        final Patient patient = Patient.getPatient( self );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser(), self.getUsername(),
                    "Retrieved demographics for user " + self.getUsername() );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Retrieves and returns the Patient with the username provided
     *
     * @param username
     *            The username of the Patient to be retrieved, as stored in the
     *            Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/patients/{username}" )
    public ResponseEntity getPatient ( @PathVariable ( "username" ) final String username ) {
        final Patient patient = Patient.getByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No Patient found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_VIEW, LoggerUtil.currentUser(), username,
                    "HCP retrieved demographics for patient with username " + username );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Creates a new Patient record for a User from the RequestBody provided.
     *
     * @param patientF
     *            the Patient to be validated and saved to the database
     * @return response
     */
    @PostMapping ( BASE_PATH + "/patients" )
    public ResponseEntity createPatient ( @RequestBody final PatientForm patientF ) {
        try {
            final Patient patient = new Patient( patientF );
            if ( null != Patient.getPatient( patient.getSelf() ) ) {
                return new ResponseEntity(
                        errorResponse( "Patient with the id " + patient.getSelf().getUsername() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            patient.save();
            LoggerUtil.log( TransactionType.CREATE_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the Patient with the id provided by overwriting it with the new
     * Patient record that is provided. If the ID provided does not match the ID
     * set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Patient to be updated
     * @param patientF
     *            The updated Patient to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/patients/{id}" )
    public ResponseEntity updatePatient ( @PathVariable final String id, @RequestBody final PatientForm patientF ) {
        // check that the user is an HCP or a patient with username equal to id
        boolean userEdit = false; // true if user edits his or her own
                                  // demographics, false if hcp edits them
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            if ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) )
                    && ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_PATIENT" ) )
                            || !auth.getName().equals( id ) ) ) {
                return new ResponseEntity( errorResponse( "You do not have permission to edit this record" ),
                        HttpStatus.UNAUTHORIZED );
            }

            userEdit = auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) ) ? true : false;
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }

        try {
            final Patient patient = new Patient( patientF );
            if ( null != patient.getSelf().getUsername() && !id.equals( patient.getSelf().getUsername() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the Patient provided" ),
                        HttpStatus.CONFLICT );
            }
            final Patient dbPatient = Patient.getByName( id );
            if ( null == dbPatient ) {
                return new ResponseEntity( errorResponse( "No Patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            patient.save();

            // Log based on whether user or hcp edited demographics
            if ( userEdit ) {
                LoggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(),
                        "User with username " + patient.getSelf().getUsername() + "updated their demographics" );
            }
            else {
                LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_EDIT, LoggerUtil.currentUser(),
                        patient.getSelf().getUsername(),
                        "HCP edited demographics for patient with username " + patient.getSelf().getUsername() );
            }
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * get a list of everyone who represents this user
     *
     * @param patientId
     *            the patient you want to know about
     * @return the list of that patient's representatives
     */
    @GetMapping ( BASE_PATH + "/patients/representatives/{patientId}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT') or hasRole('ROLE_HCP')" )
    public ResponseEntity getRepresentatives ( @PathVariable final String patientId ) {
        final Patient p = Patient.getByName( patientId );
        if ( p == null ) {
            return new ResponseEntity( "Could not find " + patientId, HttpStatus.NOT_FOUND );
        }
        else {
            for ( Patient r : p.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            return new ResponseEntity( p.getRepresentatives(), HttpStatus.OK );
        }
    }

    /**
     * get a list of everyone a patient represents
     *
     * @param patientId
     *            the patient you want to know about
     * @return the list of representees associated with that patient
     */
    @GetMapping ( BASE_PATH + "/patients/representees/{patientId}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT') or hasRole('ROLE_HCP')" )
    public ResponseEntity getRepresentees ( @PathVariable final String patientId ) {
        final Patient p = Patient.getByName( patientId );
        if ( p == null ) {
            return new ResponseEntity( "Could not find " + patientId, HttpStatus.NOT_FOUND );
        }
        else {
            for ( Patient r : p.getRepresentees() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            return new ResponseEntity( p.getRepresentees(), HttpStatus.OK );
        }
    }

    /**
     * adds a representative to a patient's list of personal representatives.
     * should map the other way as well, so the representative's list will be
     * updated as well.
     *
     * @param representee
     *            the person who's list you will add to
     * @param representative
     *            the person you will add to the list
     * @return a status saying you could or could not add this person.
     */
    @PostMapping ( BASE_PATH + "/patients/representatives/{representee}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT') or hasRole('ROLE_HCP')" )
    public ResponseEntity addRepresentative ( @PathVariable final String representee,
            @RequestBody final String representative ) {
        if ( representee.equals( representative.substring( 1, representative.length() - 1 ) ) ) {
            return new ResponseEntity( "You can't represent yourself", HttpStatus.BAD_REQUEST );
        }
        // make sure the patients exist
        Patient tee = Patient.getByName( representee );
        if ( tee == null ) {
            return new ResponseEntity( "Could not find patient with username " + representee, HttpStatus.NOT_FOUND );
        }
        final Patient tive = Patient.getByName( representative.substring( 1, representative.length() - 1 ) );
        if ( tive == null ) {
            return new ResponseEntity( "Could not find patient with username " + representative, HttpStatus.NOT_FOUND );
        }
        // now add the rep
        try {
            HashSet<Patient> newList = new HashSet<Patient>();
            newList.addAll( tee.getRepresentatives() );
            int oldSize = newList.size();
            newList.add( tive );
            if ( newList.size() == oldSize ) {
                return new ResponseEntity( representative + " is already a representative of " + representee,
                        HttpStatus.BAD_REQUEST );
            }
            tee.setRepresenatives( newList );
            tee.save();
            return new ResponseEntity(
                    "Successfully added " + representative + " as a representative of " + representee, HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "could not add representative", HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * deletes a representative from a patient's list of personal
     * representatives. mapping works both ways, so the representative will lose
     * their reference to the representee as well.
     *
     * @param representee
     *            the patient whose list will be changed
     * @param representative
     *            the patient you will add to the list
     * @return a response saying if you could or could not delete the
     *         representative
     * @throws InterruptedException
     */
    @DeleteMapping ( BASE_PATH + "/patients/representatives/{representee}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity deleteRepresentative ( @PathVariable final String representee,
            @RequestBody final String representative ) {
        final Patient tee = Patient.getByName( representee );
        if ( tee == null ) {
            return new ResponseEntity( "Could not find patient named " + representee, HttpStatus.NOT_FOUND );
        }
        Patient tive = Patient.getByName( representative.substring( 1, representative.length() - 1 ) );
        if ( tive == null ) {
            return new ResponseEntity( "Could not find patient named " + representative, HttpStatus.NOT_FOUND );
        }
        if ( !tee.inRepresentatives( tive ) ) {
            return new ResponseEntity( representative + " didn't represent " + representee + " in the first place",
                    HttpStatus.BAD_REQUEST );
        }
        try {
            HashSet<Patient> reps = new HashSet<Patient>();
            for ( Patient p : tee.getRepresentatives() ) {
                System.out.println( "comparing " + p.getSelf().getId() + " to " + tive.getSelf().getId() );
                Thread.sleep( 2000 );
                if ( !Patient.samePatient( tive, p ) ) {
                    reps.add( p );
                }
            }
            tee.setRepresenatives( reps );
            tee.save();
            return new ResponseEntity(
                    "Successfully removed " + representative + " as a representative of " + representee,
                    HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not remove representative because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
