#Author nlwrenn
#Author jgschwab
#Feature: As a developer, I want to automate
		 #our System Test plan for the personal
		 #representatives feature so that necessary
		 #functionality can easily be confirmed
		 #through the UI

#Scenario: An HCP should be able to view a patient's personal representatives
#Given I login to iTrust2 as HCP
#And a patient with personal representatives exists
#When I navigate to the patient's personal representatives
#Then I should see the patient's personal representatives

#Scenario: An HCP should be able to add a personal representative for a patient
#Given I login to iTrust2 as HCP
#And A patient exists in the system
#And I navigate to the personal representatives page
#When I assign a new personal representative for the patient
#Then the patient's personal representatives should be updated

#Scenario: A patient should be able to view their own personal representatives
#Given I login to iTrust2 as Patient
#And I have a personal representative
#When I navigate to the personal representatives page
#Then I should see my personal representatives

#Scenario Outline: A patient should be able to view who they are a representative for
#Given I login to iTrust2 as Patient
#And I am a personal representative for <name>
#When I navigate to my personal representatives page
#Then I should see that I am a personal representative for <name>
#Examples:
#| name 						|
#| personalRepresentative1	|

#Scenario Outline: A patient should be able to add a personal representative for themselves
#Given I login to iTrust2 as Patient
#And I navigate to my personal representatives page
#When I assign the personal representative <name> to myself
#Then I should see <name> as one of my personal representatives
#Examples:
#| name 						|
#| personalRepresentative2	|

#Scenario Outline: A patient should be able to remove one of their personal representatives
#Given I login to iTrust2 as Patient
#And I navigate to my personal representatives page
#And I assign the personal representative <name> to myself
#When I remove my personal representative <name>
#Then I should not see <name> as one of my personal representatives
#Examples:
#| name 						|
#| personalRepresentative3	|

#Scenario Outline: A patient should be able to undeclare themself as a personal representative
#Given I login to iTrust2 as Patient
#And I am a personal representative for <name>
#And I navigate to the page to view personal representatives for <name>
#When I undeclare myself as a personal representative for <name>
#Then I should not see myself as a personal representative for <name>
#Examples:
#| name 						|
#| personalRepresentative4	|