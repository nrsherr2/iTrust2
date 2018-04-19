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
     * @return The ProcedureStatus enum, or NOT_STARTED if there was no match.
     */
    public static ProcedureStatus parse ( final String status ) {
        if ( status.equals( "NOT_STARTED" ) ) {
            return ProcedureStatus.NOT_STARTED;
        }
        else if ( status.equals( "IN_PROGRESS" ) ) {
            return ProcedureStatus.IN_PROGRESS;
        }
        else if ( status.equals( "COMPLETE" ) ) {
            return ProcedureStatus.COMPLETE;
        }
        else {
            throw new IllegalArgumentException( "Unknown Procedure status: " + "\'" + status
                    + "\', should be \"NOT_STARTED\", \"IN_PROGRESS\", or \"COMPLETE\"." );
        }
    }
}
