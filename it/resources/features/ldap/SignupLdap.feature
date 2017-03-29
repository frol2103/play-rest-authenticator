Feature: Signup to the application with user password registered in the ldap

	Scenario: Signup happy path
		Given there is a user in ldap with email ldap.correct@test.com and password test

