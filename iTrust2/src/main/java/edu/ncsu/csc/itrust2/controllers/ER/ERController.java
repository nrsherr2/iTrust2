package edu.ncsu.csc.itrust2.controllers.ER;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Personnel to edit their information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class ERController {

	/**
	 * Landing screen for a Patient when they log in
	 *
	 * @param model
	 *            The data from the front end
	 * @return The page to show to the user
	 */
	@RequestMapping(value = "ER/index")
	@PreAuthorize("hasRole('ROLE_ER')")
	public String index(final Model model) {
		return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_ER.getLanding();
	}
}
