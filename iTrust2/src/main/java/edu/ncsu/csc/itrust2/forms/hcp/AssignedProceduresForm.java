package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form which provides an HCP with the number of lab procedures a lab tech has
 * when assigning.
 *
 * @author Nicholas Wrenn
 *
 */
public class AssignedProceduresForm implements Serializable {
    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an Office Visit form for the user
     * to fill out
     */
    public AssignedProceduresForm () {
    }

    /**
     * Lab tech whose procedures we have the number of
     */
    @NotEmpty
    private String labtech;

    /**
     * Number of procedures assigned to the lab tech
     */
    @NotEmpty
    private int    numProcedures;
}
