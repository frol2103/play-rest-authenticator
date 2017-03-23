Feature: Allow to reset password
	As a user,
	If i forget my password
	I want a way to reset it

	Scenario: Recover password happy path:
		Given I am a registered user with email recover.pwd.happy.path@test.com
		When I ask to recover the password for email recover.pwd.happy.path@test.com
		Then a 200 status code is received

		And a mail should have been sent to recover.pwd.happy.path@test.com

		When I finish the password recovery with link in mail and password testRecovered
		Then a 200 status code is received

		Given I signin with email recover.pwd.happy.path@test.com and password test
		Then a 200 status code is received
		When I ask for the current profile
		Then a 200 status code is received

