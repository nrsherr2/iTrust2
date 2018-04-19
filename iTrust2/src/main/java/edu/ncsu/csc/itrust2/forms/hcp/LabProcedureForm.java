package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.enums.LabProcedurePriority;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;

/**
 * Form which is used to add a lab procedure to an office visit.
 *
 * @author Nicholas Wrenn
 * @author Nicholas Sherrill
 *
 */
public class LabProcedureForm implements Serializable {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long    serialVersionUID = 1L;

    /**
     * office visit associated with the procedure
     */
    @NotEmpty
    private OfficeVisit          visit;

    /**
     * id of the procedure
     */
    private String               id;

    private LabProcedureCode     code;

    /**
     * the priority assigned to the procedure
     */
    @NotEmpty
    private LabProcedurePriority priority;

    /**
     * the id of the assigned lab tech
     */
    @NotEmpty
    private String               assignedLabTech;

    /**
     * the date that the lab procedure was assigned
     */
    private String               date;

    private String               comments;

    private String               status;

    /**
     * Empty constructor for filling in fields without a Prescription object.
     */
    public LabProcedureForm () {
    }

    /**
     * Gets the office visit associated with this lab procedure
     *
     * @return the ov
     */
    public OfficeVisit getOv () {
        return visit;
    }

    /**
     * Sets the office visit to be associated with this lab procedure
     *
     * @param ov
     *            the visit to set
     */
    public void setOv ( final OfficeVisit ov ) {
        this.visit = ov;
    }

    /**
     * Gets the ID of this lab procedure
     *
     * @return the id
     */
    public String getId () {
        return id;
    }

    /**
     * Sets the ID of this lab procedure
     *
     * @param id
     *            the id to set
     */
    public void setId ( final String id ) {
        this.id = id;
    }

    /**
     * Gets the priority of this lab procedure
     *
     * @return the priority
     */
    public LabProcedurePriority getLpp () {
        return priority;
    }

    /**
     * Sets the priority of this lab procedure
     *
     * @param priority
     *            the priority to set
     */
    public void setLpp ( final LabProcedurePriority priority ) {
        this.priority = priority;
    }

    /**
     * Gets the date for this lab procedure
     *
     * @return the date
     */
    public String getDate () {
        return date;
    }

    /**
     * Sets the date for this lab procedure
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Gets the comments for this lab procedure
     *
     * @return the comments
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the comments for this lab procedure
     *
     * @param comments
     *            the comments to set
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the assigned lab tech for this lab procedure
     *
     * @return the assignedTech
     */
    public String getAssignedTech () {
        return assignedLabTech;
    }

    /**
     * Sets the assigned lab tech for this lab procedure
     *
     * @param assignedTech
     *            the assignedTech to set
     */
    public void setAssignedTech ( final String assignedTech ) {
        this.assignedLabTech = assignedTech;
    }

    /**
     * Gets the procedure code for this lab procedure
     *
     * @return The procedure code for this lab procedure
     */
    public LabProcedureCode getLabProcedureCode () {
        return code;
    }

    /**
     * Sets the procedure code for this lab procedure
     *
     * @param labProcedureCode
     *            The procedure code for this lab procedure
     */
    public void setLabProcedureCode ( final LabProcedureCode labProcedureCode ) {
        this.code = labProcedureCode;
    }

    /**
     * Gets the completion status for this lab procedure
     *
     * @return The status of this lab procedure
     */
    public String getStatus () {
        return status;
    }

    /**
     * Sets the completion status for this lab procedure
     *
     * @param status
     *            The status to set for this procedure
     */
    public void setStatus ( final String status ) {
        this.status = status;
    }

}
