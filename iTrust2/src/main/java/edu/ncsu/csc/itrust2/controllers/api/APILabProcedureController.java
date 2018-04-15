package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

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
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;
import edu.ncsu.csc.itrust2.models.persistent.User;
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
    public List<ICDCode> getCodes () {
        // LoggerUtil.log( TransactionType.ICD_VIEW_ALL,
        // LoggerUtil.currentUser(), "Fetched icd codes" );
        return ICDCode.getAll();
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
            final ICDCode code = ICDCode.getById( id );
            if ( code == null ) {
                return new ResponseEntity( errorResponse( "No code with id " + id ), HttpStatus.NOT_FOUND );
            }
            LoggerUtil.log( TransactionType.ICD_VIEW, LoggerUtil.currentUser(), "Fetched icd code with id " + id );
            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not retrieve ICD Code " + id + " because of " + e.getMessage() ),
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
            // LoggerUtil.log( TransactionType.ICD_CREATE, user.getUsername(),
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
            LoggerUtil.log( TransactionType.ICD_DELETE, LoggerUtil.currentUser(),
                    user.getUsername() + " deleted an Lab Procedure code Code" );

            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not delete Lab Procedure Code " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
