#Author niwren

Feature: As a developer, I want to automate
		 our System Test plan so that necessary
		 functionality can easily be confirmed
		 through the UI 
		 
Scenario: Creating a user with the Emergency Responder role should succeed
Given the emergency responder does not exist
And I log in as admin
And I navigate to the Add User page
When I fill in the values in the Add User form to create a new emergency responder
Then the emergency responder should be created
And the emergency responder should be able to login
And they should see the emergency responder landing page

Scenario: Creating a user with the Lab Tech role should succeed
Given the emergency responder does not exist
And I log in as admin
And I navigate to the Add User page
When I fill in the values in the Add User form to create a new lab tech
Then the lab tech should be created
And the lab tech should be able to login
And they should see the emergency responder landing page

Scenario Outline: An Emergency Responder should be able to change their password
Given I login to iTrust2 as an emergency responder
And I navigate to the change password form
When I fill out the form with current password <password> and new password <newPassword>
Then My password is updated sucessfully
Examples:
	|password|newPassword|
	|123456  |654321     |	

Scenario: A Lab Tech should be able to edit their demographics
Given I login to iTrust2 as a lab tech
When I navigate to the Edit My Demographics page
When I fill in new, updated demographics
Then The demographics are updated

Scenario: An HCP should be able to view a patient's personal representatives
Given I login to iTrust2 as HCP
And a patient with personal representatives exists
When I navigate to the patient's personal representatives
Then I should see the patient's personal representatives

Scenario: An HCP should be able to add a personal representative for a patient
Given I login to iTrust2 as HCP
And A patient exists in the system
And I navigate to the personal representatives page
When I assign a new personal representative for the patient
Then the patient's personal representatives should be updated

Scenario: A patient should be able to view their own personal representatives
Given I login to iTrust2 as Patient
And I have a personal representative
When I navigate to the personal representatives page
Then then I should see my personal representative

Scenario Outline: A patient should be able to view who they are a representative for
Given I login to iTrust2 as Patient
And I am a personal representative for <name>
And the patient has a personal representative
When I navigate to the personal representatives page
When then I should see that I am a personal representative for <name>
Examples:
| name 						|
| personalRepresentative1	|

Scenario Outline: A patient should be able to add a personal representative for themselves
Given I login to iTrust2 as Patient
And I navigate to the personal representatives page
When I assign the personal representative <name> to myself
Then I should see <name> as one of my personal representatives
Examples:
| name 						|
| personalRepresentative2	|

Scenario Outline: A patient should be able to remove one of their personal representatives
Given I login to iTrust2 as Patient
And I navigate to the personal representatives page
And I assign the personal representative <name> to myself
When I remove my personal representative <name>
Then I should not see <name> as one of my personal representatives
Examples:
| name 						|
| personalRepresentative3	|

Scenario Outline: A patient should be able to remove one of their personal representatives
Given I login to iTrust2 as Patient
And I am a personal representative for <name>
And I navigate to the personal representatives page for <name>
When I undeclare myself as a personal representative for <name>
Then I should not see myself as a personal representative for <name>
Examples:
| name 						|
| personalRepresentative4	|