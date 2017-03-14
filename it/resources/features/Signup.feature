Feature: Signup to the application with user password

	Scenario: Signup to the application with no issue
		Given I signup with email signup.correct@test.com and password test
		And I signin with email signup.correct@test.com and password test

