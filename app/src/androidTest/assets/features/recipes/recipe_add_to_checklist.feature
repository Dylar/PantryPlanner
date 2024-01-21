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
    And   On Back
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

  Scenario: Create new Checklist and add two Recipes to Checklist
    Given Navi to ChecklistsPage
    And   Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Tap on ChecklistPage AddItemButton
    And   Tap on Item "SelectItem" in category "SelectCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    And   Item "SelectItem" in category "SelectCategory" has amount 1.0
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed
    And   On Back
    And   Navi to RecipesPage
    When  LongPress on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Tap on Checklist "NewChecklist"
    And   LongPress on Recipe "SharedRecipe" in category "SharedCategory"
    And   Tap on Checklist "NewChecklist"
    Then  Navi to ChecklistsPage
    And   Tap on Checklist "NewChecklist"
    And   Item "SelectItem" in category "SelectCategory" has amount 1.0
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 7.77

  # TODO make this
#  Scenario: Create new Checklist on Recipe adding
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
