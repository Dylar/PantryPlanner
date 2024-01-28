Feature: RecipeDetailsPage Items amount management

  Background:
    Given Init default Mocks
    And   Start on RecipesPage

    #CreatorRecipe
  Scenario: Increase Item amount on CreatorRecipe
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Increase Item "CreatorItem" in category "CreatorCategory" amount by 5
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 6.0
    And   On Back
    And   Tap on Confirm
    And   Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 6.0

  Scenario: Decrease Item amount on CreatorRecipe
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Decrease Item "CreatorItem" in category "CreatorCategory" amount by 5
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   On Back
    And   Tap on Confirm
    And   Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0

  Scenario: Increase shared Item amount on CreatorRecipe
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Increase Item "SharedItem" in category "SharedCategory" amount by 5
    Then  Item "SharedItem" in category "SharedCategory" has amount 7.0
    And   On Back
    And   Tap on Confirm
    And   Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "SharedItem" in category "SharedCategory" has amount 7.0

  Scenario: Decrease shared Item amount on CreatorRecipe
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Decrease Item "SharedItem" in category "SharedCategory" amount by 5
    Then  Item "SharedItem" in category "SharedCategory" has amount 0.0
    And   On Back
    And   Tap on Confirm
    And   Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "SharedItem" in category "SharedCategory" has amount 0.0

    #SharedRecipe
  Scenario: Try increasing Item amount on SharedRecipe - not editable
    Given Tap on Recipe "SharedRecipe" in category "SharedCategory"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77
    When  Increase Item "UnsharedItem" in category "UnsharedCategory" amount by 5
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77
    And   On Back
    And   Tap on Recipe "SharedRecipe" in category "SharedCategory"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77

  Scenario: Try decreasing Item amount on SharedRecipe - not editable
    Given Tap on Recipe "SharedRecipe" in category "SharedCategory"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77
    When  Decrease Item "UnsharedItem" in category "UnsharedCategory" amount by 5
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77
    And   On Back
    And   Tap on Recipe "SharedRecipe" in category "SharedCategory"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77
