package edu.ncsu.csc.itrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.HibernateUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * REST endpoints required to create, delete, and manipulate lab procedure codes
 * and lab procedures
 *
 * @author Nicholas Wrenn
 * @author Nicholas Sherrill
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APILabProcedureController extends APIController {

    /**
     * Returns a list of lab procedure codes in the system
     *
     * @return All the codes in the system
     */
    @GetMapping ( BASE_PATH + "/labcodes" )
    public List<LabProcedureCode> getCodes () {
        LoggerUtil.log( TransactionType.LAB_CODES_VIEW, LoggerUtil.currentUser(),
                "User viewed a list of all lab procedures on file" );
        return LabProcedureCode.getAll();
    }

    /**
     * Returns the lab procedure code with the given ID
     *
     * @param id
     *            The ID of the code to retrieve
     * @return The requested Code
     */
    @GetMapping ( BASE_PATH + "/labcodes/{id}" )
    public ResponseEntity getCode ( @PathVariable ( "id" ) final Long id ) {
        try {
            final LabProcedureCode code = LabProcedureCode.getById( id );
            if ( code == null ) {
                return new ResponseEntity( errorResponse( "No code with id " + id ), HttpStatus.NOT_FOUND );
            }
            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not retrieve Lab Code " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Adds a new lab procedure code to the system
     *
     * @param form
     *            The data for the new Code
     * @return The result of the action
     */
    @PostMapping ( BASE_PATH + "/labcodes" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity addCode ( @RequestBody final LabProcedureCodeForm form ) {
        try {
            final LabProcedureCode code = new LabProcedureCode( form );
            code.save();
            User user = null;
            try {
                user = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
                LoggerUtil.log( TransactionType.LAB_CODE_CREATE, user.getUsername(),
                        user.getUsername() + " created an Lab Procedure Code" );
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }

            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse(
                            "Could not create Lab Procedure Code " + form.getCode() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Updates the code with the specified ID to the value supplied.
     *
     * @param id
     *            The ID of the code to edit
     * @param form
     *            The new values for the Code
     * @return The Response of the action
     */
    @PutMapping ( BASE_PATH + "/labcodes/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity updateCode ( @PathVariable ( "id" ) final Long id,
            @RequestBody final LabProcedureCodeForm form ) {
        try {
            final LabProcedureCode code = LabProcedureCode.getById( id );
            if ( code == null ) {
                return new ResponseEntity( "No code with id " + id, HttpStatus.NOT_FOUND );
            }
            form.setId( id );
            final LabProcedureCode updatedCode = new LabProcedureCode( form );
            updatedCode.save();
            User user = null;
            try {
                user = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
                LoggerUtil.log( TransactionType.LAB_CODE_EDIT, LoggerUtil.currentUser(),
                        user.getId() + " edited code #" + id );
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }

            return new ResponseEntity( updatedCode, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update Lab Procedure Code " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes a lab procedure code from the system.
     *
     * @param id
     *            The ID of the code to delete
     * @return The result of the action.
     */
    @DeleteMapping ( BASE_PATH + "/labcodes/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity deleteCode ( @PathVariable ( "id" ) final Long id ) {
        try {
            final LabProcedureCode code = LabProcedureCode.getById( id );
            code.delete();
            User user = null;
            try {
                user = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
                LoggerUtil.log( TransactionType.LAB_CODE_DELETE, LoggerUtil.currentUser(),
                        user.getUsername() + " deleted a Lab Procedure Code" );
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }

            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not delete Lab Procedure Code " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a list of lab procedure codes in the system
     *
     * @param id
     *            The ID of the office visit to get lab procedures for
     * @return All the codes in the system
     */
    @GetMapping ( BASE_PATH + "/visit/labprocedures/{visitID}" )
    public List<LabProcedure> getLabProcedures ( @PathVariable ( "visitID" ) final Long id ) {
        // LoggerUtil.log( TransactionType.Lab_VIEW_ALL, ?
        return LabProcedure.getByVisit( id );
    }

    /**
     * Returns a specific lab procedure, based on id
     *
     * @param id
     *            The ID of the lab procedure to get
     * @return the lab procedure with the id in the system
     */
    @GetMapping ( BASE_PATH + "/labprocedures/{id}" )
    public LabProcedure getSpecificProcedure ( @PathVariable ( "id" ) final Long id ) {
        return LabProcedure.getById( id );
    }

    /**
     * Returns a hash map of the lab tech users and the number of assigned lab
     * procedures they currently have in the system
     *
     * @return map of lab techs and numProcedures for each
     */
    @GetMapping ( BASE_PATH + "/labtechs" )
    public List<List> getLabTechs () {
        final Session session = HibernateUtil.openSession();
        final List<User> techs = User.getByRole( Role.ROLE_LABTECH );
        final List<Integer> numbers = new ArrayList<Integer>();

        for ( final User user : User.getByRole( Role.ROLE_LABTECH ) ) {
            try {
                session.beginTransaction();
                final List procedureNums = session.createCriteria( LabProcedure.class )
                        .add( Restrictions.like( "assignedLabTech", user.getUsername() ) ).list();
                numbers.add( procedureNums.size() );
            }
            finally {
                try {
                    session.getTransaction().commit();
                }
                catch ( final Exception e ) {
                    e.printStackTrace( System.out );
                    // Continue
                }
            }
        }

        session.close();

        final List<List> ret = new ArrayList<List>();
        ret.add( techs );
        ret.add( numbers );

        return ret;
    }

    /**
     * returns a list of assigned lab procedures to the front end. returns only
     * a list of what is assigned to the designated tech.
     *
     * @return a list of lab procedures assigned to the currently logged in lab
     *         tech
     */
    @GetMapping ( BASE_PATH + "/labprocedures" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public ResponseEntity getForTech () {
        return new ResponseEntity( LabProcedure.getByTech( LoggerUtil.currentUser() ), HttpStatus.OK );
    }

    /**
     * A lab tech can edit the details of a lab procedure after it has been
     * assigned to them. This will input the info and save it.
     *
     * @param lpf
     *            the form with all of the new information
     * @param id
     *            The ID of the lab procedure to update
     * @return a response saying whether or not the operation was successful
     */
    @PutMapping ( BASE_PATH + "/labprocedures/{id}" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public ResponseEntity editLabProcedure ( @PathVariable final Long id, @RequestBody final LabProcedureForm lpf ) {
        try {
            final LabProcedure lp = new LabProcedure( lpf );
            if ( lp.getId() != null && !id.equals( lp.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the LabProcedure provided" ),
                        HttpStatus.CONFLICT );
            }
            final LabProcedure dbLP = LabProcedure.getById( id );
            if ( dbLP == null ) {
                return new ResponseEntity( errorResponse( "No lab procedure with id " + id + " found" ),
                        HttpStatus.NOT_FOUND );
            }
            User user = null;
            try {
                user = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }
            dbLP.delete();
            lp.save();
            LoggerUtil.log( TransactionType.LAB_PROCEDURE_EDIT, LoggerUtil.currentUser(),
                    user.getId() + " edited lab procedure #" + lp.getId() );
            return new ResponseEntity( lp, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "could not update " + lpf.toString() + " because " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
