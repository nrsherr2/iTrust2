package edu.ncsu.csc.itrust2.models.enums;

public enum ProcedureStatus {
    /** the procedure has not started */
    NOT_STARTED("Started" ),
    /** the procedure is being worked on */
    IN_PROGRESS("In Progress"),
    /** the procedure is done */
    COMPLETE("Complete");
    
    /** the text associated with the status */
    private String text;
    
    /**
     * constructor.
     * 
     * @param desc description of the status
     */
    private ProcedureStatus(final String desc) {
        this.text = desc;
    }
    /**
     * return the text of the status
     * @return the text
     */
    public String getText() {
        return text;
    }
}
