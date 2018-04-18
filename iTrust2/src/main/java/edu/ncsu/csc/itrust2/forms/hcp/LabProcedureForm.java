package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.enums.LabProcedurePriority;
import edu.ncsu.csc.itrust2.models.enums.ProcedureStatus;
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
    private OfficeVisit          ov;

    /**
     * id of the procedure
     */
    private String               id;

    private LabProcedureCode     labProcedureCode;

    /**
     * the priority assigned to the procedure
     */
    @NotEmpty
    private LabProcedurePriority lpp;

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

    private ProcedureStatus      status;

    /**
     * Empty constructor for filling in fields without a Prescription object.
     */
    public LabProcedureForm () {
    }

    /**
     * @return the ov
     */
    public OfficeVisit getOv () {
        return ov;
    }

    /**
     * @param ov
     *            the ov to set
     */
    public void setOv ( final OfficeVisit ov ) {
        this.ov = ov;
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
     * @return the lpp
     */
    public LabProcedurePriority getLpp () {
        return lpp;
    }

    /**
     * @param lpp
     *            the lpp to set
     */
    public void setLpp ( final LabProcedurePriority lpp ) {
        this.lpp = lpp;
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
        return labProcedureCode;
    }

    public void setLabProcedureCode ( final LabProcedureCode labProcedureCode ) {
        this.labProcedureCode = labProcedureCode;
    }

    public ProcedureStatus getStatus () {
        return status;
    }

    public void setStatus ( ProcedureStatus status ) {
        this.status = status;
    }

}
