Feature: Payees API
  As a user I want to manage payees with category associations

  Scenario: Create a payee with categories
    Given a category "Groceries" with icon "shopping-cart" exists
    And a category "Clothing" with icon "shirt" exists
    When I create a payee "Dunnes Stores" with all existing categories
    Then the response status is 201
    And the response contains payee name "Dunnes Stores"
    And the response contains 2 categories

  Scenario: List payees returns empty when none exist
    When I list payees
    Then the response status is 200
    And the response is an empty list

  Scenario: List payees with categories
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I list payees
    Then the response status is 200
    And the response list has 1 items

  Scenario: Create payee with duplicate name returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I create a payee "Dunnes Stores" with all existing categories
    Then the response status is 409

  Scenario: Create payee with non-existent category returns 422
    When I create a payee "Dunnes Stores" with a random category ID
    Then the response status is 422

  Scenario: Create payee without categories returns 422
    When I create a payee "Dunnes Stores" with empty categories
    Then the response status is 422

  Scenario: Update payee name
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I update the payee name to "Tesco"
    Then the response status is 200
    And the response contains payee name "Tesco"

  Scenario: Update payee categories
    Given a category "Groceries" with icon "shopping-cart" exists
    And a category "Clothing" with icon "shirt" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I update the payee categories to all existing categories
    Then the response status is 200
    And the response contains 2 categories

  Scenario: Update non-existent payee returns 404
    When I update payee with random ID name to "Tesco"
    Then the response status is 404

  Scenario: Delete a payee
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I delete the payee
    Then the response status is 204

  Scenario: Delete non-existent payee returns 404
    When I delete payee with random ID
    Then the response status is 404

  Scenario: Create payee without name returns 422
    Given a category "Groceries" with icon "shopping-cart" exists
    When I create a payee "" with all existing categories
    Then the response status is 422

  Scenario: Duplicate payee name case-insensitive returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I create a payee "dunnes stores" with all existing categories
    Then the response status is 409

  Scenario: Update payee with conflicting name returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    And a payee "Tesco" with category "Groceries" exists
    When I update the payee name to "Dunnes Stores"
    Then the response status is 409

  Scenario: Update payee categories to non-existent category returns 422
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I update the payee categories to a random category ID
    Then the response status is 422

  Scenario: Payee response includes full category details
    Given a category "Groceries" with icon "shopping-cart" exists
    And a payee "Dunnes Stores" with category "Groceries" exists
    When I list payees
    Then the response status is 200
    And the first payee category has name "Groceries" and icon "shopping-cart"
