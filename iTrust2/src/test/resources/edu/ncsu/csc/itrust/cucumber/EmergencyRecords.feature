#Author amshafe2

Feature: As a developer, I want to automate
		 our System Test plan for the Emergency Health Records
		 so that necessary functionality can easily
		 be confirmed through the UI 
		 
Scenario Outline: HCP views emergency health records
Given There is a patient with the name: <MID>
And I log into iTrust2 as an HCP
And I navigate to the Emergency Health Records Page
When I fill in the username of the patient
Then the patients medical information is displayed
Examples:
	|MID    |
	|AliceThirteen|

Scenario Outline: ER views emergency health records
Given There is a patient with the name: <MID>
And I log into iTrust2 as an ER
And I navigate to the Emergency Health Records Page
When I fill in the username of the patient
Then the patients medical information is displayed
Examples:
	|MID    |
	|AliceThirteen|
	