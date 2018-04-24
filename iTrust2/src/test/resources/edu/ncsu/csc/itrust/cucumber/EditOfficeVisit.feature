#Author nlwrenn

Feature: Edit office visit
	As an iTrust2 HCP
	I want to edit an office visit
	So that the record of a Patient visiting the doctor
	is kept up to date
	
Scenario: Delete a lab procedure from an office visit
Given The required facilities exist
And I log in to iTrust2 as a HCP
And I navigate to the Document Office Visit page
And I fill in information on the office visit
And The office visit is documented successfully
And The lab procedure was created
And I navigate to the Edit Office Visit page
When I delete the lab procedure from the office visit
Then the lab procedure should no longer exist