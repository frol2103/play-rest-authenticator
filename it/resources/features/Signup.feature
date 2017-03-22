Feature: Signup to the application with user password

	Scenario: Signup happy path
		Given I signup with email signup.correct@test.com password test firstname foo and lastname bar
		Then a 200 status code is received
		And a mail should have been sent to signup.correct@test.com
		When I visit the link in the mail
		Then a 200 status code is received
		Given I signin with email signup.correct@test.com and password test
		Then a 200 status code is received
		When I ask for the current profile
		Then a 200 status code is received
		And repsonse should have .firstName equals to foo


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
		And there is an error error.path.missing for .password

	Scenario: Try to signup with an inexisting user
		Given I signin with email not_existing_account@test.be and password test
		Then a 400 status code is received
		And an INVALID_CREDENTIALS error should be thrown

	Scenario: Try to signup with a wrong password
		Given I signup with email wrong_password@test.com and password test
		Given I signin with email wrong_password@test.com and password test_wrong
		Then a 400 status code is received
		And an INVALID_CREDENTIALS error should be thrown

	Scenario: Ask for profile before signin
		When I ask for the current profile
		Then a 401 status code is received
		And an UNAUTHORIZED_EXCEPTION error should be thrown
