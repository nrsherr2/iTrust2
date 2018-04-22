#Author nlwrenn

Feature: Lab Procedure Codes and Lab Procedures
	As an iTrust2 Admin and Lab Tech
	I want to create, edit, and delete lab procedure
	codes and lab procedures associated with office visits
	
Scenario: Creating a lab procedure code should succeed
Given I login iTrust2 as an admin
And I navigate to the lab procedure codes page
When I create a lab procedure code
Then the lab procedure code should be created successfully
And I delete the lab procedure code

Scenario: Editing a lab procedure code should succeed
Given I login iTrust2 as an admin
And I navigate to the lab procedure codes page
And I create a lab procedure code
And the lab procedure code should be created successfully
When I edit the lab procedure code
Then the lab procedure code should be updated
And I delete the lab procedure code

Scenario: Deleting a lab procedure code should succeed
Given I login iTrust2 as an admin
And I navigate to the lab procedure codes page
And I create a lab procedure code
And the lab procedure code should be created successfully
When I delete the lab procedure code
Then the lab procedure code should no longer exist

Scenario: Updating a lab procedure should succeed
Given The required facilities exist
And I log in to iTrust2 as a HCP
And I navigate to the Document Office Visit page
And I fill in information on the office visit
And I logout
And I login as a lab tech with a lab procedure
And I navigate to the lab procedures page
When I update the lab procedure
Then the lab procedure should be updated


Scenario: Reassigning a lab procedure should succeed
Given The required facilities exist
And I log in to iTrust2 as a HCP
And I navigate to the Document Office Visit page
And I fill in information on the office visit
And I logout
And I login as a lab tech with a lab procedure
And I navigate to the lab procedures page
When I reassign the lab procedure
Then the lab procedure should be reassigned