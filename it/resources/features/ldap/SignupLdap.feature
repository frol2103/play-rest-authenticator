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


	Scenario: Try to signin with a wrong password
		Given there is a user in ldap with email ldap.wrong_password@test.com and password test
		Given I signin with ldap email ldap.wrong_password@test.com and password test_wrong
		Then a 400 status code is received
		And an INVALID_CREDENTIALS error should be thrown
