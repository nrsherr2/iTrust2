package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;
import java.util.ArrayList;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureCodeForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
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
        // LoggerUtil.log( TransactionType.Lab_VIEW_ALL,
        // LoggerUtil.currentUser(), "Fetched icd codes" );
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
            // TODO: ADD LOGGING
            // LoggerUtil.log( TransactionType.Lab_VIEW,
            // LoggerUtil.currentUser(), "Fetched icd code with id " + id );
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
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }
            // LoggerUtil.log( TransactionType.Lab_CREATE, user.getUsername(),
            // user.getUsername() + " created an Lab Procedure Code" );

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
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }
            // TODO: Add Logging
            // LoggerUtil.log( TransactionType.Lab_DELETE,
            // LoggerUtil.currentUser(),
            // user.getUsername() + " deleted an Lab Procedure code Code" );

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
     * @return All the codes in the system
     */
    @GetMapping ( BASE_PATH + "/visit/labprocedures/{visitID}" )
    public List<LabProcedure> getLabProcedures ( @PathVariable ( "visitID" ) final Long id ) {
        // LoggerUtil.log( TransactionType.Lab_VIEW_ALL,
        return LabProcedure.getByVisit( id );
    }

    @DeleteMapping ( BASE_PATH + "/delete/labprocedures/{visitId]/{id}" )
    public ResponseEntity deleteProcedure ( @PathVariable ( "visitID" ) final Long vistId,
            @PathVariable ( "id" ) final Long id ) {
        try {
            final LabProcedure procedure = LabProcedure.getById( id );
            procedure.delete();
            User user = null;
            try {
                user = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
            }
            catch ( final Exception e ) {
                // ignore, its was a test that wasn't authenticated properly.
            }
            // TODO: Add Logging
            // LoggerUtil.log( TransactionType.Lab_DELETE,
            // LoggerUtil.currentUser(),
            // user.getUsername() + " deleted an Lab Procedure code Code" );

            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not delete Lab Procedure " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a specific lab procedure, based on id
     *
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

        List<List> ret = new ArrayList<List>();
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
    @PreAuthorize ( "hasRole('ROLE_LABTECH'" )
    public ResponseEntity getForTech () {
        return new ResponseEntity( LabProcedure.getByTech( LoggerUtil.currentUser() ), HttpStatus.OK );
    }

}
