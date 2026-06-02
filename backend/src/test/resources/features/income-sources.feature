Feature: Income Sources

  Scenario: List income sources when none exist
    Given no income sources exist
    When I list income sources
    Then the response status is 200
    And the response contains 0 income sources
    And the pagination shows 0 total elements

  Scenario: Create income source
    When I create an income source with name "Salary" and amount "3500.00" currency "EUR"
    Then the response status is 201
    And the response contains income source name "Salary"
    And the response contains income source amount "3500.00" currency "EUR"
    And the response contains an income source id
    And the response contains an income source createdAt

  Scenario: Create income source with all fields
    When I create an income source:
      | name            | Salary                     |
      | description     | Monthly salary from Corp X |
      | amount          | 3500.00                    |
      | currency        | EUR                        |
      | frequency       | MONTHLY                    |
      | paymentDateType | FIXED                      |
      | paymentDateRule | 25                         |
      | startDate       | 2026-01-01                 |
    Then the response status is 201
    And the response contains income source name "Salary"
    And the response contains income source description "Monthly salary from Corp X"

  Scenario: Create income source with missing required fields
    When I create an income source with empty body
    Then the response status is 400

  Scenario: Create income source with missing amount
    When I create an income source with name "Salary" and no amount
    Then the response status is 400

  Scenario: Create income source with invalid currency
    When I create an income source with name "Salary" and amount "3500.00" currency "EU"
    Then the response status is 422

  Scenario: Create income source with negative amount
    When I create an income source with name "Salary" and amount "-100.00" currency "EUR"
    Then the response status is 422

  Scenario: Create income source with invalid frequency
    When I create an income source with invalid frequency "DAILY"
    Then the response status is 400

  Scenario: Create income source with invalid payment date type
    When I create an income source with invalid payment date type "FLOATING"
    Then the response status is 400

  Scenario: List income sources after creation
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    And an income source exists with name "Freelance" and amount "700.00" currency "EUR"
    When I list income sources
    Then the response status is 200
    And the response contains 2 income sources
    And the pagination shows 2 total elements

  Scenario: Pagination returns correct metadata
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    And an income source exists with name "Freelance" and amount "700.00" currency "EUR"
    When I list income sources with page 1 and size 1
    Then the response status is 200
    And the response contains 1 income sources
    And the pagination shows 2 total elements
    And the pagination shows 2 total pages

  Scenario: Update income source amount
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    When I update the income source with amount "4000.00" currency "EUR"
    Then the response status is 200
    And the response contains income source amount "4000.00" currency "EUR"
    And the response contains income source name "Salary"

  Scenario: Update income source name
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    When I update the income source with name "Monthly Salary"
    Then the response status is 200
    And the response contains income source name "Monthly Salary"
    And the response contains income source amount "3500.00" currency "EUR"

  Scenario: Deactivate income source
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    When I deactivate the income source
    Then the response status is 200
    And the response contains income source isActive false

  Scenario: Update non-existent income source
    When I update income source "00000000-0000-0000-0000-000000000000" with name "Test"
    Then the response status is 404

  Scenario: Update income source with negative amount
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    When I update the income source with amount "-50.00" currency "EUR"
    Then the response status is 422

  Scenario: Delete income source
    Given an income source exists with name "Salary" and amount "3500.00" currency "EUR"
    When I delete the income source
    Then the response status is 204
    And the income source no longer exists

  Scenario: Delete non-existent income source
    When I delete income source "00000000-0000-0000-0000-000000000000"
    Then the response status is 404
