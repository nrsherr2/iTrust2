package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enumerates the priority levels for a lab procedure
 *
 * @author Justin Schwab
 *
 */
public enum LabProcedurePriority {

    /**
     * This field is unrelated
     */
    NONAPPLICABLE ( 0 ),
    /**
     * highest priority
     */
    PRIORITY_1 ( 1 ),
    /**
     * priority 2
     */
    PRIORITY_2 ( 2 ),
    /**
     * priority 3
     */
    PRIORITY_3 ( 3 ),
    /**
     * lowest priority
     */
    PRIORITY_4 ( 4 ),

    ;

    /**
     * Code of the Priority
     */
    private int code;

    /**
     * Create a Priority from the numerical code.
     *
     * @param code
     *            Code of the Status
     */
    private LabProcedurePriority ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the Priority
     *
     * @return Code of the Priority
     */
    public int getCode () {
        return code;
    }

    /**
     * Returns the HouseholdSmokingStatus enum that matches the given code.
     *
     * @param code
     *            The code to match
     * @return Corresponding HouseholdSmokingStatus object.
     */
    public static LabProcedurePriority parseValue ( final int code ) {
        for ( final LabProcedurePriority status : values() ) {
            if ( status.getCode() == code ) {
                return status;
            }
        }
        return LabProcedurePriority.NONAPPLICABLE;
    }
}
