package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;

/**
 * Form which is used to add a lab procedure to an office visit.
 *
 * @author Nicholas Wrenn
 *
 */
public class LabProcedureForm implements Serializable {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    private String            drug;
    private int               dosage;
    private String            startDate;

    /**
     * Empty constructor for filling in fields without a Prescription object.
     */
    public LabProcedureForm () {
    }

}
