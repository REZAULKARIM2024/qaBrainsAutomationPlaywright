Feature: Login Functionality
  As a user
  I want to login to the application
  So that I can access my account

  Background:
    Given User launches the application

  @smoke @regression @Login
  Scenario: Login with valid credentials
    When User enters valid username and password
    Then User should be logged in successfully

  @regression @Login
  Scenario: Login with invalid credentials
    When User enters invalid username and password
    Then Error message should be displayed