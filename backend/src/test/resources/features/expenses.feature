Feature: Expenses API
  As a user I want to manage expenses within financial months

  Background:
    Given the application is configured with month start day 1
    And a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    And a financial month exists for year 2026 month 6

  Scenario: Create an expense
    When I create an expense "Weekly shop" with expected value 120.00 due on "2026-06-07"
    Then the response status is 201
    And the expense title is "Weekly shop"
    And the expense status is "PENDING"
    And the expense quarter number is 1

  Scenario: Create expense assigns to correct quarter
    When I create an expense "Late month" with expected value 50.00 due on "2026-06-25"
    Then the response status is 201
    And the expense quarter number is 4

  Scenario: List expenses for a month
    Given an expense "Shop 1" with expected value 100.00 due on "2026-06-05" exists
    And an expense "Shop 2" with expected value 80.00 due on "2026-06-15" exists
    When I list expenses for the financial month
    Then the response status is 200
    And the response list has 2 items

  Scenario: Update expense to record payment
    Given an expense "Weekly shop" with expected value 120.00 due on "2026-06-07" exists
    When I update the expense with actual value 115.50 and payment date "2026-06-07"
    Then the response status is 200
    And the expense status is "PAID"

  Scenario: Delete an expense
    Given an expense "Weekly shop" with expected value 120.00 due on "2026-06-07" exists
    When I delete the expense
    Then the response status is 204

  Scenario: Delete non-existent expense returns 404
    When I delete expense with random ID
    Then the response status is 404

  Scenario: Create expense with invalid payee returns 422
    When I create an expense with random payee due on "2026-06-07"
    Then the response status is 422

  Scenario: Create expense with due date outside month returns 422
    When I create an expense "Outside" with expected value 50.00 due on "2026-07-15"
    Then the response status is 422

  Scenario: List expenses for non-existent month returns 404
    When I list expenses for random month ID
    Then the response status is 404
