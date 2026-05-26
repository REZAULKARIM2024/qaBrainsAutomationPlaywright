Feature: User Registration
  As a new user
  I want to register an account
  So that I can access the application

  Background:
    Given User launches the application
    And User is on homepage

  @smoke @regression @Registration
  Scenario: Successful user registration with valid email
    When User navigates to registration page
    And User enters valid registration details with email "rezaulkarimqa25@gmail.com"
    And User clicks on register button
    Then User should be registered successfully

  @regression @Registration
  Scenario: Registration should fail with invalid email
    When User navigates to registration page
    And User enters registration details with email "rezaulkarimqa25gmail.com"
    And User clicks on register button
    Then Email validation error message should be displayed