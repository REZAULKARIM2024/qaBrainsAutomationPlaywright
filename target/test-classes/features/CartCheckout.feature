Feature: Cart Functionality
  As a user
  I want to manage products in the cart
  So that I can complete my purchase smoothly

  Background:
    Given User launches the application

  @smoke @regression
  Scenario: Add product to cart
    When User adds products to cart
    Then Product should appear in cart

  @smoke @regression
  Scenario: Remove product from cart
    When User adds products to cart
    And User removes product from cart
    Then Cart should be empty