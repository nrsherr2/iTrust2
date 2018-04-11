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
        List<Patient> pats = Patient.getPatients();
        for ( Patient p : pats ) {
            p.setRepresenatives( new HashSet<Patient>() );
            p.setRepresentees( new HashSet<Patient>() );
        }
        return pats;
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
            for ( Patient p : patient.getRepresentatives() ) {
                p.setRepresenatives( new HashSet<Patient>() );
                p.setRepresentees( new HashSet<Patient>() );
            }
            for ( Patient p : patient.getRepresentees() ) {
                p.setRepresenatives( new HashSet<Patient>() );
                p.setRepresentees( new HashSet<Patient>() );
            }
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
            for ( Patient p : patient.getRepresentatives() ) {
                p.setRepresenatives( new HashSet<Patient>() );
                p.setRepresentees( new HashSet<Patient>() );
            }
            for ( Patient p : patient.getRepresentees() ) {
                p.setRepresenatives( new HashSet<Patient>() );
                p.setRepresentees( new HashSet<Patient>() );
            }
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
     * get a list of everyone who represents this user. this is the call for
     * HCPs. patients will have a different one
     *
     * @param patientId
     *            the patient you want to know about
     * @return the list of that patient's representatives
     */
    @GetMapping ( BASE_PATH + "/patients/representatives/{patientId}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
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
            LoggerUtil.log( TransactionType.HCP_VIEW_REPRESENTATIVES, LoggerUtil.currentUser(), patientId );
            return new ResponseEntity( p.getRepresentatives(), HttpStatus.OK );
        }
    }

    /**
     * gets a list of everyone who represents this user. this is the call for
     * patients. hcps have a different one.
     * 
     * @return the representative list for the currently logged in patient
     */
    @GetMapping ( BASE_PATH + "/patients/representatives" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getRepresentativesPatient () {
        final Patient currentPatient = Patient.getByName( LoggerUtil.currentUser() );
        if ( currentPatient == null ) {
            return new ResponseEntity( "Current patient cannot be found", HttpStatus.NOT_FOUND );
        }
        else {
            for ( Patient r : currentPatient.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.VIEW_REPRESENTATIVES, LoggerUtil.currentUser() );
            return new ResponseEntity( currentPatient.getRepresentatives(), HttpStatus.OK );
        }
    }

    /**
     * get a list of everyone a patient represents. this is the HCP call.
     *
     * @param patientId
     *            the patient you want to know about
     * @return the list of representees associated with that patient
     */
    @GetMapping ( BASE_PATH + "/patients/representees/{patientId}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
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
            LoggerUtil.log( TransactionType.HCP_VIEW_REPRESENTEES, LoggerUtil.currentUser(), patientId );
            return new ResponseEntity( p.getRepresentees(), HttpStatus.OK );
        }
    }

    /**
     * get a list of everyone a patient represents. this is the patient call.
     * 
     * @return the list of representees for the currently logged in user
     */
    @GetMapping ( BASE_PATH + "/patients/representees" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getRepresenteesPatient () {
        final Patient currentPatient = Patient.getByName( LoggerUtil.currentUser() );
        if ( currentPatient == null ) {
            return new ResponseEntity( "Current patient cannot be found", HttpStatus.NOT_FOUND );
        }
        else {
            for ( Patient r : currentPatient.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.VIEW_REPRESENTEES, LoggerUtil.currentUser() );
            return new ResponseEntity( currentPatient.getRepresentatives(), HttpStatus.OK );
        }
    }

    /**
     * as a patient, call this method to add a representative for yourself
     * 
     * @param rep
     *            the MID of the patient you want to add
     * @return your new list of representatives
     */
    @PostMapping ( BASE_PATH + "/patients/representatives" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity addRepresentativePatient ( @RequestBody String rep ) {
        // trim the string because I'm not about that long line life
        rep = rep.substring( 1, rep.length() - 1 );
        String curr = LoggerUtil.currentUser();
        if ( rep.equals( curr ) ) {
            return new ResponseEntity( "You can't represent yourself", HttpStatus.BAD_REQUEST );
        }
        // make sure these guys exist
        Patient representee = Patient.getByName( curr );
        if ( representee == null ) {
            return new ResponseEntity( "Could not find patient with username " + curr, HttpStatus.NOT_FOUND );
        }
        Patient representative = Patient.getByName( rep );
        if ( representative == null ) {
            return new ResponseEntity( "Could not find patient with username " + rep, HttpStatus.NOT_FOUND );
        }
        // now add the rep
        try {
            HashSet<Patient> reps = new HashSet<Patient>();
            reps.addAll( representee.getRepresentatives() );
            int oldSize = reps.size();
            reps.add( representative );
            if ( reps.size() == oldSize ) {
                return new ResponseEntity( rep + " is already a representative of " + curr, HttpStatus.BAD_REQUEST );
            }
            representee.setRepresenatives( reps );
            representee.save();
            for ( Patient r : representee.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.ADD_REPRESENTATIVE, curr, rep );
            return new ResponseEntity( representee.getRepresentatives(), HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not add representative because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }

    }

    @PostMapping ( BASE_PATH + "/patients/representees" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity addRepresenteePatient ( @RequestBody String rep ) {
        // trim the string because I'm not about that long line life
        rep = rep.substring( 1, rep.length() - 1 );
        String curr = LoggerUtil.currentUser();
        if ( rep.equals( curr ) ) {
            return new ResponseEntity( "You can't represent yourself", HttpStatus.BAD_REQUEST );
        }
        // make sure these guys exist
        Patient representee = Patient.getByName( curr );
        if ( representee == null ) {
            return new ResponseEntity( "Could not find patient with username " + curr, HttpStatus.NOT_FOUND );
        }
        Patient representative = Patient.getByName( rep );
        if ( representative == null ) {
            return new ResponseEntity( "Could not find patient with username " + rep, HttpStatus.NOT_FOUND );
        }
        // now add the rep
        try {
            HashSet<Patient> reps = new HashSet<Patient>();
            reps.addAll( representative.getRepresentees() );
            int oldSize = reps.size();
            reps.add( representee );
            if ( reps.size() == oldSize ) {
                return new ResponseEntity( curr + " is already a representative of " + rep, HttpStatus.BAD_REQUEST );
            }
            representative.setRepresentees( reps );
            representative.save();
            for ( Patient r : representative.getRepresentees() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.ADD_REPRESENTEE, curr, rep );
            return new ResponseEntity( representative.getRepresentees(), HttpStatus.OK );

        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not add representee because " + e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * adds a representative to a patient's list of personal representatives.
     * should map the other way as well, so the representative's list will be
     * updated as well. This is the call for the HCP
     *
     * @param representee
     *            the person who's list you will add to
     * @param representative
     *            the person you will add to the list
     * @return the updated list of representatives for the representee parameter
     */
    @PostMapping ( BASE_PATH + "/patients/representatives/{representee}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
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
            for ( Patient r : tee.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.HCP_ADD_REPRESENTATIVE, LoggerUtil.currentUser(), representative,
                    "added " + representee );
            return new ResponseEntity( tee.getRepresentatives(), HttpStatus.OK );
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
     * @return the new list of representatives for the representee parameter
     */
    @DeleteMapping ( BASE_PATH + "/patients/representatives/{representee}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
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
                if ( !Patient.samePatient( tive, p ) ) {
                    reps.add( p );
                }
            }
            tee.setRepresenatives( reps );
            tee.save();
            for ( Patient r : tee.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.HCP_DELETE_REPRESENTATIVE, LoggerUtil.currentUser(), representative,
                    "deleted " + representee );
            return new ResponseEntity( tee.getRepresentatives(), HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not remove representative because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }

    @DeleteMapping ( BASE_PATH + "/patients/representatives" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity deleteRepresentativePatient ( @RequestBody String rep ) {
        // trim the string because I'm not about that long line life
        rep = rep.substring( 1, rep.length() - 1 );
        // check they exist
        Patient representative = Patient.getByName( rep );
        if ( representative == null ) {
            return new ResponseEntity( "Could not find patient " + rep, HttpStatus.NOT_FOUND );
        }
        Patient representee = Patient.getByName( LoggerUtil.currentUser() );
        if ( representee == null ) {
            return new ResponseEntity( "Could not find patient " + LoggerUtil.currentUser(), HttpStatus.NOT_FOUND );
        }
        if ( !representee.inRepresentatives( representative ) ) {
            return new ResponseEntity( "patient was never represented in the first place", HttpStatus.BAD_REQUEST );
        }
        // and delete
        try {
            HashSet<Patient> reps = new HashSet<Patient>();
            for ( Patient p : representee.getRepresentatives() ) {
                if ( !Patient.samePatient( p, representative ) ) {
                    reps.add( p );
                }
            }
            representee.setRepresenatives( reps );
            representee.save();
            for ( Patient r : representee.getRepresentatives() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.DELETE_REPRESENTATIVE, LoggerUtil.currentUser(), rep );
            return new ResponseEntity( representee.getRepresentatives(), HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not remove representative because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * when logged in as a patient, use this mapping to delete a patient from
     * your list of representees
     * 
     * @param rep
     *            the MID of the patient you want to delete
     * @return the updated list of representees
     */
    @DeleteMapping ( BASE_PATH + "/patients/representees" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity deleteRepresenteePatient ( @RequestBody String rep ) {
        // trim the string because I'm not about that long line life
        rep = rep.substring( 1, rep.length() - 1 );
        // check that they exist
        Patient representative = Patient.getByName( LoggerUtil.currentUser() );
        if ( representative == null ) {
            return new ResponseEntity( "Could not find patient " + rep, HttpStatus.NOT_FOUND );
        }
        Patient representee = Patient.getByName( rep );
        if ( representee == null ) {
            return new ResponseEntity( "Could not find patient " + LoggerUtil.currentUser(), HttpStatus.NOT_FOUND );
        }
        if ( !representee.inRepresentatives( representative ) ) {
            return new ResponseEntity( "patient was never represented in the first place", HttpStatus.BAD_REQUEST );
        }
        // now delete
        try {
            HashSet<Patient> reps = new HashSet<Patient>();
            for ( Patient p : representative.getRepresentees() ) {
                if ( !Patient.samePatient( p, representee ) ) {
                    reps.add( p );
                }
            }
            representative.setRepresentees( reps );
            representative.save();
            for ( Patient r : representative.getRepresentees() ) {
                r.setRepresenatives( new HashSet<Patient>() );
                r.setRepresentees( new HashSet<Patient>() );
            }
            LoggerUtil.log( TransactionType.DELETE_REPRESENTEE, LoggerUtil.currentUser(), rep );
            return new ResponseEntity( representative.getRepresentees(), HttpStatus.OK );
        }
        catch ( Exception e ) {
            return new ResponseEntity( "Could not remove representee because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
