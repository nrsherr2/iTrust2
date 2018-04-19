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
     * @return the ov
     */
    public OfficeVisit getOv () {
        return visit;
    }

    /**
     * @param ov
     *            the visit to set
     */
    public void setOv ( final OfficeVisit ov ) {
        this.visit = ov;
    }

    /**
     * @return the id
     */
    public String getId () {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId ( final String id ) {
        this.id = id;
    }

    /**
     * @return the priority
     */
    public LabProcedurePriority getLpp () {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setLpp ( final LabProcedurePriority priority ) {
        this.priority = priority;
    }

    /**
     * @return the date
     */
    public String getDate () {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * @return the comments
     */
    public String getComments () {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * @return the assignedTech
     */
    public String getAssignedTech () {
        return assignedLabTech;
    }

    /**
     * @param assignedTech
     *            the assignedTech to set
     */
    public void setAssignedTech ( final String assignedTech ) {
        this.assignedLabTech = assignedTech;
    }

    public LabProcedureCode getLabProcedureCode () {
        return code;
    }

    public void setLabProcedureCode ( final LabProcedureCode labProcedureCode ) {
        this.code = labProcedureCode;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus ( final String status ) {
        this.status = status;
    }

}