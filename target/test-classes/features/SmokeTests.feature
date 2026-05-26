Feature: Smoke Test Suite
  As a QA team
  We want to verify critical application flows
  So that we can ensure the build is stable

  @smoke
  Scenario: S-01 Verify Home page loads
    Given User launches the application
    Then Home page should load successfully

  @smoke
  Scenario: S-02 Verify navigation links
    Given User launches the application
    When User clicks on Catalog, About and Blog
    Then Pages should navigate correctly

  @smoke
  Scenario: S-03 Verify wishlist and refer a friend functionality
    Given User launches the application
    When User clicks on Wish list and Refer a Friend
    Then Pages should open successfully