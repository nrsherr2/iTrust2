package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.LabProcedurePriority;

/**
 * Class to represent a Lab Procedure which ias associated with an office visit
 * and a lab tech.
 *
 * @author Nicholas Wrenn
 * @author Nicholas Sherrill
 *
 */
@Entity
@Table ( name = "LabProcedure" )
public class LabProcedure extends DomainObject<LabProcedure> {

    /** Hibernate/Thymeleaf need empty constructors */
    public LabProcedure () {

    }

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "visit_id", nullable = false )
    private OfficeVisit          visit;

    private ArrayList<String>    comments;

    @NotNull
    private LabProcedurePriority priority;

    @NotNull
    private String               assignedLabTech;

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                 id;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "code_id" )
    private LabProcedureCode     code;

    @NotNull
    private boolean              completed;

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the Diagnosis
     *
     * @param id
     *            The new ID of the Diagnosis. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Sets the Office Visit this diagnosis is associated with
     *
     * @param visit
     *            The Visit to associate with
     */
    public void setVisit ( final OfficeVisit visit ) {
        this.visit = visit;
    }

    /**
     * Returns the visit this diagnosis is part of
     *
     * @return The Office Visit
     */
    public OfficeVisit getVisit () {
        return visit;
    }

    /**
     * Sets the note for the diagnosis
     *
     * @param n
     *            The new note
     */
    public void setComments ( final ArrayList<String> n ) {
        this.comments = n;
    }

    /**
     * Returns the note of the diagnosis
     *
     * @return The note
     */
    public ArrayList<String> getComments () {
        return comments;
    }

    /**
     * Sets the code for this diagnosis
     *
     * @param code
     *            The new code
     */
    public void setCode ( final LabProcedureCode code ) {
        this.code = code;
    }

    /**
     * Returns the code for this diagnosis
     *
     * @return The code
     */
    public LabProcedureCode getCode () {
        return code;
    }

    /**
     * Returns the priority assigned to the Lab Procedure
     *
     * @return the assigned priority
     */
    public LabProcedurePriority getPriority () {
        return priority;
    }

    /**
     * Used to set the priority for the lab procedure
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority ( LabProcedurePriority priority ) {
        this.priority = priority;
    }

    /**
     * Used to retrieve the username of the assigned lab tech
     *
     * @return username of assigned lab tech
     */
    public String getAssignedLabTech () {
        return assignedLabTech;
    }

    /**
     * Used to set the username of the lab tech assigned
     *
     * @param assignedLabTech
     *            username of lab tech to assign
     */
    public void setAssignedLabTech ( String assignedLabTech ) {
        this.assignedLabTech = assignedLabTech;
    }

    /**
     * Returns a List of Diagnosis that are selected by the given WHERE clause
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of diagnoses SELECTed
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabProcedure> getWhere ( final List<Criterion> where ) {
        return (List<LabProcedure>) getWhere( LabProcedure.class, where );
    }

    /**
     * Returns the Lab Procedure with the given ID
     *
     * @param id
     *            The ID of the Lab Procedure to retrieve
     * @return The requested lab procedure.
     */
    public static LabProcedure getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Return a list of Lab Procedures for the specified visit
     *
     * @param id
     *            The ID of the Office Visit to search for
     * @return The list of Diagnoses
     */
    public static List<LabProcedure> getByVisit ( final Long id ) {
        return getWhere( createCriterionAsList( "visit", OfficeVisit.getById( id ) ) );
    }
}
