package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

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
    private OfficeVisit          ov;

    /**
     * id of the procedure
     */
    private String               id;

    /**
     * the code that describes the lab procedure code
     */
    @NotEmpty
    private LabProcedureCode     lpc;

    /**
     * the priority assigned to the procedure
     */
    @NotEmpty
    private LabProcedurePriority lpp;

    private Calendar             date;

    private List<String>         comments;

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
     * @param ov the ov to set
     */
    public void setOv ( OfficeVisit ov ) {
        this.ov = ov;
    }

    /**
     * @return the id
     */
    public String getId () {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId ( String id ) {
        this.id = id;
    }

    /**
     * @return the lpc
     */
    public LabProcedureCode getLpc () {
        return lpc;
    }

    /**
     * @param lpc the lpc to set
     */
    public void setLpc ( LabProcedureCode lpc ) {
        this.lpc = lpc;
    }

    /**
     * @return the lpp
     */
    public LabProcedurePriority getLpp () {
        return lpp;
    }

    /**
     * @param lpp the lpp to set
     */
    public void setLpp ( LabProcedurePriority lpp ) {
        this.lpp = lpp;
    }

    /**
     * @return the date
     */
    public Calendar getDate () {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate ( Calendar date ) {
        this.date = date;
    }

    /**
     * @return the comments
     */
    public List<String> getComments () {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments ( List<String> comments ) {
        this.comments = comments;
    }

}
