Feature: Categories API
  As a user I want to manage expense categories with icons

  Scenario: Create a category successfully
    When I create a category with name "Groceries" and icon "shopping-cart"
    Then the response status is 201
    And the response contains name "Groceries"
    And the response contains icon "shopping-cart"

  Scenario: List categories returns empty when none exist
    When I list categories
    Then the response status is 200
    And the response is an empty list

  Scenario: List categories returns created categories
    Given a category "Groceries" with icon "shopping-cart" exists
    And a category "Utilities" with icon "bolt" exists
    When I list categories
    Then the response status is 200
    And the response list has 2 items

  Scenario: Create category with duplicate name returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    When I create a category with name "Groceries" and icon "basket"
    Then the response status is 409

  Scenario: Update a category name
    Given a category "Groceries" with icon "shopping-cart" exists
    When I update the category name to "Food"
    Then the response status is 200
    And the response contains name "Food"

  Scenario: Update a category icon
    Given a category "Groceries" with icon "shopping-cart" exists
    When I update the category icon to "basket"
    Then the response status is 200
    And the response contains icon "basket"

  Scenario: Update category with conflicting name returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    And a category "Utilities" with icon "bolt" exists
    When I update the last category name to "Groceries"
    Then the response status is 409

  Scenario: Delete a category
    Given a category "Groceries" with icon "shopping-cart" exists
    When I delete the category
    Then the response status is 204
    And the category no longer exists

  Scenario: Delete non-existent category returns 404
    When I delete category with random ID
    Then the response status is 404

  Scenario: Create category without name returns 422
    When I create a category with name "" and icon "shopping-cart"
    Then the response status is 422

  Scenario: Create category without icon returns 422
    When I create a category with name "Groceries" and icon ""
    Then the response status is 422

  Scenario: Update non-existent category returns 404
    When I update category with random ID name to "Food"
    Then the response status is 404

  Scenario: Create category with name exceeding 100 chars returns 422
    When I create a category with a name of 101 characters and icon "shopping-cart"
    Then the response status is 422

  Scenario: Update with empty body is a no-op
    Given a category "Groceries" with icon "shopping-cart" exists
    When I update the category with empty body
    Then the response status is 200
    And the response contains name "Groceries"
    And the response contains icon "shopping-cart"

  Scenario: List categories returns them ordered by name
    Given a category "Utilities" with icon "bolt" exists
    And a category "Groceries" with icon "shopping-cart" exists
    When I list categories
    Then the response status is 200
    And the first category name is "Groceries"
    And the second category name is "Utilities"

  Scenario: Create category with duplicate name case-insensitive returns 409
    Given a category "Groceries" with icon "shopping-cart" exists
    When I create a category with name "groceries" and icon "basket"
    Then the response status is 409
