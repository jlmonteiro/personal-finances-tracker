Feature: Financial Months API
  As a user I want to create financial months with auto-generated quarters

  Background:
    Given the application is configured with month start day 1

  Scenario: Create a financial month
    When I create a financial month for year 2026 month 6
    Then the response status is 201
    And the financial month starts on "2026-06-01"
    And the financial month ends on "2026-06-30"

  Scenario: Create generates 4 quarters
    When I create a financial month for year 2026 month 6
    And I get the quarters for the created month
    Then the response status is 200
    And there are 4 quarters
    And quarter 1 starts on "2026-06-01"
    And quarter 4 ends on "2026-06-30"

  Scenario: Create duplicate month returns 409
    Given a financial month exists for year 2026 month 6
    When I create a financial month for year 2026 month 6
    Then the response status is 409

  Scenario: List financial months
    Given a financial month exists for year 2026 month 6
    And a financial month exists for year 2026 month 7
    When I list financial months
    Then the response status is 200
    And the response list has 2 items

  Scenario: Get financial month by ID
    Given a financial month exists for year 2026 month 6
    When I get the financial month by ID
    Then the response status is 200
    And the financial month starts on "2026-06-01"

  Scenario: Get non-existent financial month returns 404
    When I get financial month with random ID
    Then the response status is 404

  Scenario: Get quarters for non-existent month returns 404
    When I get quarters for financial month with random ID
    Then the response status is 404

  Scenario: Create month with start day 15
    Given the application is configured with month start day 15
    When I create a financial month for year 2026 month 6
    Then the response status is 201
    And the financial month starts on "2026-06-15"
    And the financial month ends on "2026-07-14"
