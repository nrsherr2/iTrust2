#Author nlwrenn
#Author jgschwab
Feature: As a developer, I want to automate
		 our System Test plan for the personal
		 representatives feature so that necessary
		 functionality can easily be confirmed
		 through the UI

Scenario Outline: A patient should be able to view and add their own personal representatives
Given I log in to iTrust2 with my username <name>
When I navigate to the personal representatives page
And I assign <other> as a personal representative
Then I should see <other> as my personal representative

Examples:
| name                      | other                 |
| AliceThirteen             | TimTheOneYearOld      |

Scenario Outline: A patient should be able to view who they are a representative for
Given I log in to iTrust2 with my username <name>
When I navigate to the personal representatives page
And I assign <other> as a personal representative
And I log out
And I log in to iTrust2 with my username <other>
And I navigate to the personal representatives page
Then I should see that I am a personal representative for <name>

Examples:
| name 						| other                 |
| TimTheOneYearOld	        | BobTheFourYearOld     |

Scenario Outline: A patient should be able to remove one of their personal representatives
Given the required patients exist in the database
And I log in to iTrust2 with my username <name>
When I navigate to the personal representatives page
And I assign <other> as a personal representative
And I remove my personal representative <other>
Then <name> should not see <other> as a personal representative
Examples:
| name 						| other                 |
| BobTheFourYearOld	        | AliceThirteen         |

Scenario Outline: A patient should be able to undeclare themself as a personal representative
Given the required patients exist in the database
And I log in to iTrust2 with my username <name>
When I navigate to the personal representatives page
And I assign <other> as a personal representative
And I log out
And I log in to iTrust2 with my username <other>
And I navigate to the personal representatives page
And I undeclare myself as a personal representative for <name>
Then <name> should not see <other> as a personal representative
Examples:
| name 						| other                 |
| BobTheFourYearOld	        | TimTheOneYearOld      |