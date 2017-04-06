@Only
Feature: Signup to the application with user password registered in the ldap

	Scenario: Signup happy path
		Given there is a user in ldap with email ldap.correct@test.com password test firstname Malcolm and lastname Reynolds
		Given I signin with ldap email ldap.correct@test.com and password test
		Then a 200 status code is received
		When I ask for the current profile
		Then a 200 status code is received
		And repsonse should have .firstName equals to Malcolm
		And repsonse should have .lastName equals to Reynolds


