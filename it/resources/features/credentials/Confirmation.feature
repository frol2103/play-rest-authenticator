Feature: To guarantee the email enterred by the user is correct,
  we should send him an email with a token that will allow him to confirm its email and thus validate its account.

  Until the token is entered the account should be unusable.



  Scenario: Signin with not confirmed user
    Given I signup with email not_confirmed_user@test.com and password test
    Then a 200 status code is received
    Given I signin with email not_confirmed_user@test.com and password test
    Then a 400 status code is received
    Then a USER_NOT_CONFIRMED error should be thrown

  Scenario: confirm with a wrong token
    Given I signup with email wrong_signup_token@test.com and password test
    Then a 200 status code is received
    And a mail should have been sent to wrong_signup_token@test.com
    When I visit the link in the mail with altered last char
    Then a 400 status code is received
    Then a TOKEN_ERROR error should be thrown
    Given I signin with email wrong_signup_token@test.com and password test
    Then a 400 status code is received
    Then a USER_NOT_CONFIRMED error should be thrown

  Scenario: confirm with a outdated token
    Given I signup with email outdated_token@test.com and password test
    And the signup token for outdated_token@test.com expired yesterday
    And a mail should have been sent to outdated_token@test.com
    When I visit the link in the mail
    Then a 400 status code is received
    Then a EXPIRED_TOKEN error should be thrown
    Given I signin with email outdated_token@test.com and password test
    Then a 400 status code is received
    Then a USER_NOT_CONFIRMED error should be thrown
