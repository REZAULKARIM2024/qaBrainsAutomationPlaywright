Feature: Forgot Password functionality
  As a user
  I want to reset my password
  So that I can regain access to my account

  @smoke @regression @ForgotPassword
  Scenario: Registered user can reset password
    Given User is on Forgot Password page
    When User enters registered email "rezaulkarimqa25@gmail.com"
    And User clicks on Submit button
    Then User should see success message "Password reset successfully"

  @regression @ForgotPassword
  Scenario: Unregistered user cannot reset password
    Given User is on Forgot Password page
    When User enters unregistered email "rezaulkarimqa25gmail.com"
    And User clicks on Submit button
    Then User should see error message "Please enter a valid email address"