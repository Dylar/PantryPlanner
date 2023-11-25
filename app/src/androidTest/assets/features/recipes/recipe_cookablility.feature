Feature: RecipeDetailsPage check cookability

  Background:
    Given Init default Mocks
    And   Start on RecipesPage
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is cookable
    And   Recipe "SharedRecipe" in category "SharedCategory" is NOT cookable

  Scenario: Check cookability on each recipe details
    When  Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    Then  Recipe is cookable
    And   Tap on FloatingActionButton
    And   CookButton is displayed
    And   On Back
    When  Tap on Recipe "SharedRecipe" in category "SharedCategory"
    Then  Recipe is NOT cookable
    And   Tap on FloatingActionButton
    And   CookButton is NOT displayed

  Scenario: Cook a recipe and remove Items from Stock
    Given Navi to StocksPage
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
    And   Navi to RecipesPage
    When  Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    And   Tap on CookButton
    # TODO select stock (first with selectionDialog then with tabs?)
    And   Tap on Confirm
    Then  Recipe is NOT cookable
    And   On Back
    And   Navi to StocksPage
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   Item "SharedItem" in category "SharedCategory" has amount 0.5

# TODO
#  Scenario: Create new Checklist with Recipe
#  Scenario: Add Recipe to Checklist
