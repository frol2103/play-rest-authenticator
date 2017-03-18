Feature: Signup to the application with user password

	Scenario: Signup happy path
		Given I signup with email signup.correct@test.com and password test
		Then a 200 status code is received
		And I signin with email signup.correct@test.com and password test
		Then a 200 status code is received


	Scenario: Signup when user already exists
		Given I signup with email signup.already_exist@test.com and password test1
		Then a 200 status code is received
		Given I signup with email signup.already_exist@test.com and password test1
		Then a 400 status code is received
		Then a USER_EXISTS error should be thrown

	Scenario: Try to signup with no password
		Given I signup with email nopass@test.be and password -
		Then a 400 status code is received
		And an INCORRECT_DATA error should be thrown
		And there is an error error.path.missing for /password