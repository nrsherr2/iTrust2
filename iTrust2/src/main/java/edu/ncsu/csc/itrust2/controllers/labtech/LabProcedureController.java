package edu.ncsu.csc.itrust2.controllers.labtech;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * This controller enables Labtechs to modify Lab Procedures in the system
 *
 * @author Tam Le
 * @author Justin Schwab
 *
 */
@Controller
public class LabProcedureController {
    /**
     * Add procedure
     *
     * @param model
     *            data for front end
     * @return mapping
     */
    @RequestMapping ( value = "labtech/procedures" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String addProcedure ( final Model model ) {
        return "/labtech/procedures";
    }
}
