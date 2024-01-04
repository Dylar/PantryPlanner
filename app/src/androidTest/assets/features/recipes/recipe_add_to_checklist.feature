Feature: Add Recipe to Checklist

  Background:
    Given Init default Mocks
    And   Start on RecipesPage
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is cookable
    And   Recipe "SharedRecipe" in category "SharedCategory" is NOT cookable

  Scenario: On RecipeDetailsPage add Recipe to Checklist
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Recipe is cookable
    When  Tap on BuyButton
    Then  SelectChecklistDialog is displayed
    And   Checklist "CreatorChecklist" is displayed
    And   Checklist "SharedChecklist" is displayed
    When  Tap on Checklist "CreatorChecklist"
    Then  SelectChecklistDialog is NOT displayed
    And   SnackBar shown: "Zutaten wurden \"CreatorChecklist\" hinzugefügt"
    When  On Back
    And   Navi to ChecklistsPage
    And   Tap on Checklist "CreatorChecklist"
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 2.0
    And   Item "SharedItem" in category "SharedCategory" has amount 4.0

  Scenario: On RecipesPage add Recipe to Checklist
    Given LongPress on Recipe "CreatorRecipe" in category "CreatorCategory"
    Then  SelectChecklistDialog is displayed
    And   Checklist "CreatorChecklist" is displayed
    And   Checklist "SharedChecklist" is displayed
    When  Tap on Checklist "CreatorChecklist"
    Then  SelectChecklistDialog is NOT displayed
    And   SnackBar shown: "Zutaten wurden \"CreatorChecklist\" hinzugefügt"
    And   Navi to ChecklistsPage
    And   Tap on Checklist "CreatorChecklist"
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 2.0
    And   Item "SharedItem" in category "SharedCategory" has amount 4.0

#  Scenario: Create new Checklist with Recipe
#    Given Navi to StocksPage
#    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
#    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
#    And   Navi to RecipesPage
#    When  Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
#    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
#    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
#    And   Tap on CookButton
#    # TODO select stock (first with selectionDialog then with tabs?)
#    And   Tap on Confirm
#    Then  Recipe is NOT cookable
#    And   On Back
#    And   Navi to StocksPage
#    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0
#    And   Item "SharedItem" in category "SharedCategory" has amount 0.5
