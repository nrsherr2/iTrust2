#Author nlwrenn
#Author jgschwab
Feature: As a developer, I want to automate
		 our System Test plan for the new users feature
		 so that necessary functionality can easily
		 be confirmed through the UI 
		 
Scenario: Creating a user with the Emergency Responder role should succeed
Given the emergency responder does not exist
And I log in as the admin
And I navigate to the add user page
When I fill in the values of the Add User form to create a new emergency responder
Then the emergency responder should be created
And the emergency responder should be able to login
And they should see the emergency responder landing page

Scenario: Creating a user with the Lab Tech role should succeed
Given the lab tech does not exist
And I log in as the admin
And I navigate to the add user page
When I fill in the values of the Add User form to create a new lab tech
Then the lab tech should be created
And the lab tech should be able to login
And they should see the lab tech landing page

Scenario Outline: An Emergency Responder should be able to change their password
Given I login to iTrust2 as an emergency responder
And I navigate to the change password form
When I fill out the ERs form with current password <password> and new password <newPassword>
Then My password is updated successfully
Examples:
	|password|newPassword|
	|123456  |654321     |	

Scenario: A Lab Tech should be able to edit their demographics
Given I login to iTrust2 as a lab tech
When I navigate to the edit my demographics page
When I fill in new demographics
Then The demographics are successfully updated