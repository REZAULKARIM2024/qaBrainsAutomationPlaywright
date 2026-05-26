Feature: Regression Test Suite
  As a QA team
  We want to verify core functionalities
  So that the system remains stable after changes

  @regression
  Scenario: R-01 Add multiple products to cart
    Given User launches the application
    When User adds products to cart
    Then Products should be added successfully

  @regression
  Scenario: R-02 Update product quantity
    Given User launches the application
    When User updates product quantity
    Then Quantity should be updated

  @regression
  Scenario: R-03 Search invalid item
    Given User launches the application
    When User searches for invalid item
    Then No result should be displayed