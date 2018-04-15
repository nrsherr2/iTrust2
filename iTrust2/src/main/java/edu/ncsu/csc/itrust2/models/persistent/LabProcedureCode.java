package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureCodeForm;

/**
 * Class for Diagnosis codes. These codes themselves are stored as a String,
 * along with a description and an ID.
 *
 * @author Nicholas Wrenn
 *
 */
@Entity
@Table ( name = "LabProcedureCodes" )
public class LabProcedureCode extends DomainObject<LabProcedure> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    /**
     * The Lab Procedure code
     */
    private String code;
    /**
     * Description of the lab procedure
     */
    private String description;

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Empty constructor for Hibernate
     */
    public LabProcedureCode () {

    }

    /**
     * Construct a Lab Procedure Code from a form
     *
     * @param form
     *            The form that validates and sanitizes input
     */
    public LabProcedureCode ( final LabProcedureCodeForm form ) {
        setCode( form.getCode() );
        setDescription( form.getDescription() );
        setId( form.getId() );

        // validate
        if ( description.length() > 250 ) {
            throw new IllegalArgumentException( "Description too long (250 characters max): " + description );
        }
        // code in the LOINC format standard, DDDDD-D
        final char[] c = code.toCharArray();
        if ( c.length < 7 || c.length > 7 ) {
            throw new IllegalArgumentException( "Code must be seven characters: " + code );
        }
        if ( !Character.isDigit( c[0] ) || !Character.isDigit( c[1] ) || !Character.isDigit( c[2] )
                || !Character.isDigit( c[3] ) || !Character.isDigit( c[4] ) ) {
            throw new IllegalArgumentException( "First five characters must be digits: " + code );
        }
        // check its a valid number
        if ( c[5] != '-' ) {
            throw new IllegalArgumentException( "Sixth character of code must be a hyphen: " + code );
        }
        if ( !Character.isDigit( c[6] ) ) {
            throw new IllegalArgumentException( "Seventh character of code must be a digit: " + code );
        }
    }

    /**
     * Sets the ID of the Code
     *
     * @param id
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the String representation of the code
     *
     * @return The code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the String representation of the code
     *
     * @param code
     *            The new code
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Returns the description of the code
     *
     * @return The description
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Sets the description of this code
     *
     * @param d
     *            The new description
     */
    public void setDescription ( final String d ) {
        description = d;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof LabProcedureCode ) {
            final LabProcedureCode c = (LabProcedureCode) o;
            return id.equals( c.getId() ) && description.equals( c.getDescription() ) && code.equals( c.getCode() );
        }
        return false;
    }

    /**
     * Returns a List of Lab Procedure Codes that meet the given WHERE clause
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of Codes selected
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabProcedureCode> getWhere ( final List<Criterion> where ) {
        return (List<LabProcedureCode>) getWhere( ICDCode.class, where );
    }

    /**
     * Returns the Code with the given ID
     *
     * @param id
     *            The ID to retrieve
     * @return The Lab Procedure Codes requested if it exists
     */
    public static LabProcedureCode getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }

    }

    /**
     * Returns a list of all Lab Procedure code in the system
     *
     * @return The list of Codes
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabProcedureCode> getAll () {
        return (List<LabProcedureCode>) DomainObject.getAll( ICDCode.class );
    }

}
