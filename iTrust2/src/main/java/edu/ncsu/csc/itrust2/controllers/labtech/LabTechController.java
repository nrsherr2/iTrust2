package edu.ncsu.csc.itrust2.controllers.labtech;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Labtech to edit their information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class LabTechController {

    /**
     * Landing screen for a Patient when they log in
     *
     * @param model
     *            The data from the front end
     * @return The page to show to the user
     */
    @RequestMapping ( value = "labtech/index" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_LABTECH.getLanding();
    }

    /**
     * Landing screen for a Patient when they log in
     *
     * @param model
     *            The data from the front end
     * @return The page to show to the user
     */
    @RequestMapping ( value = "labtech/viewEmergencyRecords" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" ) 
    public String viewEmergencyRecords ( final Model model ) {
        return "labtech/viewEmergencyHealthRecords";
    }
}
