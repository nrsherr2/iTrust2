package edu.ncsu.csc.itrust2.cucumber;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;

public class LabProcedureStepDefs {

	private final WebDriver driver = new HtmlUnitDriver(true);
	private final String baseUrl = "http://localhost:8080/iTrust2";
	WebDriverWait wait = new WebDriverWait(driver, 2);

	private final String LAB_CODE = "10191-2";
	private final String NEW_DESC = "new lpCode";
	private String search;

	@Before
	public void setup() {
		User labtech = new User("labtech2", "123456", Role.ROLE_LABTECH, 1);
		labtech.save();
	}

	/**
	 * Admin log in
	 */
	@When("I login iTrust2 as an admin")
	public void adminLogin() {
		driver.get(baseUrl);
		final WebElement username = driver.findElement(By.name("username"));
		username.clear();
		username.sendKeys("admin");
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys("123456");
		final WebElement submit = driver.findElement(By.className("btn"));
		submit.click();
	}

	/**
	 * Admin log in
	 */
	@When("I login as a lab tech with a lab procedure")
	public void labTechLogin() {
		driver.get(baseUrl);
		final WebElement username = driver.findElement(By.name("username"));
		username.clear();
		username.sendKeys("labtech");
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys("123456");
		final WebElement submit = driver.findElement(By.className("btn"));
		submit.click();
	}

        @When("I now logout of itrust")
	public void logoutLabProc() {
	    driver.get(baseUrl);
	    try {
		final WebElement logout = driver.findElement(By.id("logout"));
		logout.click();
	    } catch (Exception e) {
		throw new AssertionError(e.getMessage() + driver.getPageSource());
	    }
	}

	@When("I navigate to the lab procedure codes page")
	public void navigateLabProcedures() {
		((JavascriptExecutor) driver).executeScript("document.getElementById('manageLabProcedureCodes').click();");
	}

	@When("I navigate to the lab procedures page")
	public void navigateProcedures() {
		((JavascriptExecutor) driver).executeScript("document.getElementById('procedures').click();");
	}

	@When("I create a lab procedure code")
	public void createLabProcedureCode() {
		String search = "code";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement codeElement = driver.findElement(By.name(search));
		codeElement.clear();
		codeElement.sendKeys(LAB_CODE);

		search = "description";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement descElement = driver.findElement(By.name(search));
		descElement.sendKeys("Office Visit testing Lab Procedure Code");

		search = "submit";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement sub = driver.findElement(By.name(search));
		sub.click();

	}

	@Then("the lab procedure code should be created successfully")
	public void assertLabProcedureCode() {
		boolean found = false;
		final List<LabProcedureCode> list = LabProcedureCode.getAll();
		for (final LabProcedureCode l : list) {
			if (l.getCode().equals(LAB_CODE)) {
				found = true;
			}
		}
		try {
		    assertTrue(found);
		} catch (Exception e) {
		    String m = "";
		    for (final LabProcedureCode l : list) {
			m += "" + l.getCode() + "\n";
		    }
		    throw new AssertionError(e.getMessage() + m);
		}
	}

	@Then("I delete the lab procedure code")
	public void deleteLabProcedureCode() {
		final List<LabProcedureCode> list = LabProcedureCode.getAll();
		for (final LabProcedureCode l : list) {
			if (l.getCode().equals(LAB_CODE)) {
			    // TODO
			    l.delete();
			}
		}
	}

	@When("I edit the lab procedure code")
	public void editLabProcedureCode() {
		// TODO when in dev
		String search = "code";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement codeElement = driver.findElement(By.name(search));
		codeElement.clear();
		codeElement.sendKeys(LAB_CODE);

		search = "description";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement desc = driver.findElement(By.name(search));
		desc.sendKeys(NEW_DESC);

		search = "submit";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement sub = driver.findElement(By.name(search));
		sub.click();

	}

	@Then("the lab procedure code should be updated")
	public void assertLabProcCodeUp() {
		boolean found = false;
		final List<LabProcedureCode> list = LabProcedureCode.getAll();
		for (final LabProcedureCode l : list) {
			if (l.getCode().equals(LAB_CODE) && l.getDescription().equals(NEW_DESC)) {
				found = true;
			}
		}
		try {
		    assertTrue(found);
		} catch (AssertionError e) {
		    String m = "";
		    for (final LabProcedureCode l : list) {
			m += "" + l.getCode() + "\n";
		    }
		    throw new AssertionError(e.getMessage() + m);
		}
	}

	@Then("the lab procedure code should no longer exist")
	public void assertLabCodeDeleted() {
		boolean found = false;
		final List<LabProcedureCode> list = LabProcedureCode.getAll();
		for (final LabProcedureCode l : list) {
			if (l.getCode().equals(LAB_CODE)) {
				found = true;
			}
		}
		try {
		    assertFalse(found);
		} catch (Exception e) {
		    String m = "";
		    for (final LabProcedureCode l : list) {
			m += "" + l.getCode() + "\n";
		    }
		    throw new AssertionError(e.getMessage() + m);
		}
	}

	@When("I update the lab procedure")
	public void updateLabProcedure() {
		search = "selectProcedure";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement codeElement = driver.findElement(By.name(search));
		codeElement.click();

		// changet the description
		search = "notes";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement noteElement = driver.findElement(By.name(search));
		noteElement.clear();
		noteElement.sendKeys(NEW_DESC);

		search = "procSave";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement submit = driver.findElement(By.name(search));
		submit.click();
	}

	@Then("the lab procedure should be updated")
	public void assertLabCodeUpdated() {
		boolean found = false;
		final List<LabProcedure> list = LabProcedure.getByTech("labtech");
		for (final LabProcedure l : list) {
			if (l.getComments().equals(NEW_DESC)) {
				found = true;
			}
		}
		try {
		    assertTrue(found);
		} catch (AssertionError e) {
		    String m = "";
		    for (final LabProcedure l : list) {
			m += "" + l.getComments() + "\n";
		    }
		    throw new AssertionError(e.getMessage() + m);
		}

	}

	@When("I reassign the lab procedure")
	public void reassignLabProcedure() {
		try {
		search = "selectProcedure";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement codeElement = driver.findElement(By.name(search));
		codeElement.click();

		// changet the assignment
		search = "lt-labtech2";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement techElement = driver.findElement(By.name(search));
		techElement.click();

		search = "procSave";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(search)));
		final WebElement submit = driver.findElement(By.name(search));
		submit.click();
		} catch (Exception e) {
			throw new AssertionError(e.getMessage() + driver.getPageSource());
		}
	}

	@Then("the lab procedure should be reassigned")
	public void assertLabCodeReassigned() {
		boolean found = false;
		final List<LabProcedure> list = LabProcedure.getByTech("labtech2");
		found = list.size() > 0 ? true : false;		
		assertTrue(found);
	}

}
