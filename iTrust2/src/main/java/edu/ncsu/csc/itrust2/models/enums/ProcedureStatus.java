package edu.ncsu.csc.itrust2.models.enums;

public enum ProcedureStatus {
    /** the procedure has not started */
    NOT_STARTED ( "Not Started" ),
    /** the procedure is being worked on */
    IN_PROGRESS ( "In Progress" ),
    /** the procedure is done */
    COMPLETE ( "Complete" );

    /** the text associated with the status */
    private String text;

    /**
     * constructor.
     *
     * @param desc
     *            description of the status
     */
    private ProcedureStatus ( final String desc ) {
        this.text = desc;
    }

    /**
     * return the text of the status
     *
     * @return the text
     */
    public String getText () {
        return text;
    }

    /**
     * Finds a ProcedureStatus enum from the string provided.
     *
     * @param state
     *            Name of the ProcedureStatus to find
     * @return The ProcedureStatus enum, or North Carolina (NC) if there was no
     *         match.
     */
    public static ProcedureStatus parse ( final String status ) {
        for ( final ProcedureStatus myStatus : values() ) {
            if ( myStatus.getText().equals( status ) ) {
                return myStatus;
            }
        }
        return ProcedureStatus.NOT_STARTED;
    }
}
