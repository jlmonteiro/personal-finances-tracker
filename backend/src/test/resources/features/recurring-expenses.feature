Feature: Recurring Expenses API
  As a user I want to manage recurring expense templates

  Background:
    Given the application is configured with month start day 1
    And a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists

  Scenario: Create a recurring expense
    When I create a recurring expense "Weekly groceries" with value 120.00 frequency "MONTHLY"
    Then the response status is 201
    And the recurring expense title is "Weekly groceries"
    And the recurring expense is active

  Scenario: List recurring expenses
    Given a recurring expense "Weekly groceries" with value 120.00 exists
    When I list recurring expenses
    Then the response status is 200
    And the response list has 1 items

  Scenario: Update a recurring expense
    Given a recurring expense "Weekly groceries" with value 120.00 exists
    When I update the recurring expense value to 150.00
    Then the response status is 200
    And the recurring expense value is 150.00

  Scenario: Deactivate a recurring expense
    Given a recurring expense "Weekly groceries" with value 120.00 exists
    When I deactivate the recurring expense
    Then the response status is 200
    And the recurring expense is not active

  Scenario: Delete a recurring expense
    Given a recurring expense "Weekly groceries" with value 120.00 exists
    When I delete the recurring expense
    Then the response status is 204

  Scenario: Delete non-existent recurring expense returns 404
    When I delete recurring expense with random ID
    Then the response status is 404
