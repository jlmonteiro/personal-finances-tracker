Feature: Application Configuration

  Scenario: Get configuration when none exists
    Given no configuration exists
    When I request the configuration
    Then the response status is 404

  Scenario: Get configuration when it exists
    Given configuration exists with currency "EUR" and month start day 1
    When I request the configuration
    Then the response status is 200
    And the response contains currency "EUR"
    And the response contains month start day 1

  Scenario: Create configuration
    Given no configuration exists
    When I create configuration with currency "EUR" and month start day 1
    Then the response status is 201
    And the response contains currency "EUR"
    And the response contains month start day 1
    And the response contains an id
    And the response contains a createdAt timestamp

  Scenario: Create configuration with invalid currency too short
    Given no configuration exists
    When I create configuration with currency "EU" and month start day 1
    Then the response status is 422

  Scenario: Create configuration with invalid currency too long
    Given no configuration exists
    When I create configuration with currency "EURO" and month start day 1
    Then the response status is 422

  Scenario: Create configuration with month start day too low
    Given no configuration exists
    When I create configuration with currency "EUR" and month start day 0
    Then the response status is 422

  Scenario: Create configuration with month start day too high
    Given no configuration exists
    When I create configuration with currency "EUR" and month start day 30
    Then the response status is 422

  Scenario: Create configuration with empty body
    Given no configuration exists
    When I create configuration with empty body
    Then the response status is 400

  Scenario: Create configuration when already exists
    Given configuration exists with currency "EUR" and month start day 1
    When I create configuration with currency "GBP" and month start day 15
    Then the response status is 409

  Scenario: Update configuration currency
    Given configuration exists with currency "EUR" and month start day 1
    When I update configuration with currency "GBP"
    Then the response status is 200
    And the response contains currency "GBP"
    And the response contains month start day 1

  Scenario: Update configuration month start day
    Given configuration exists with currency "EUR" and month start day 1
    When I update configuration with month start day 15
    Then the response status is 200
    And the response contains currency "EUR"
    And the response contains month start day 15

  Scenario: Update configuration when none exists
    Given no configuration exists
    When I update configuration with currency "GBP"
    Then the response status is 404

  Scenario: Update configuration with invalid currency
    Given configuration exists with currency "EUR" and month start day 1
    When I update configuration with currency "EURO"
    Then the response status is 422

  Scenario: Update configuration with invalid month start day
    Given configuration exists with currency "EUR" and month start day 1
    When I update configuration with month start day 0
    Then the response status is 422
