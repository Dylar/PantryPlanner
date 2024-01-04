Feature: RecipesPage Recipe management

  Background:
    Given Init default Mocks
    And   Start on RecipesPage
    And   Recipe "NewRecipe" in category "NewCategory" is NOT displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is displayed
    And   Recipe "SharedRecipe" in category "SharedCategory" is displayed

#TODO filter for cookable

  Scenario: Create a new Recipe
    Given Tap on NewRecipeButton
    And   RecipeDetailsPage rendered
    And   "RecipeDetailsPage" shared with none
    And   No Items displayed
    When  Input "NewRecipe" as Recipe name
    And   Input "NewCategory" as Recipe category
    And   "RecipeDetailsPage" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    And   Tap on RecipeDetailsPage AddItemButton
    And   SelectItemsPage rendered
    And   Tap on Item "SelectItem" in category "SelectCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    And   RecipeDetailsPage rendered
    And   Tap on SaveRecipeButton
    Then  RecipesPage rendered
    And   Recipe "NewRecipe" in category "NewCategory" is displayed
    And   Tap on Recipe "NewRecipe" in category "NewCategory"
    And   RecipeDetailsPage rendered
    And   Recipe name is "NewRecipe"
    And   Recipe category is "NewCategory"
    And   "RecipeDetailsPage" shared with "Mohammed Lee"
    And   Item "SelectItem" in category "SelectCategory" is displayed

  Scenario: Try to remove a Recipe, but cancel confirmation
    When  Swipe to remove Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Tap on dismiss
    Then  Recipe "CreatorRecipe" in category "CreatorCategory" is displayed

  Scenario: Remove a Recipe
    When  Swipe to remove Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Tap on Confirm
    Then  Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed
    And   On Back
    And   Navi to RecipesPage
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed

  Scenario: Remove a shared Recipe
    Given Swipe to remove Recipe "SharedRecipe" in category "SharedCategory"
    And   Tap on Confirm
    Then  Recipe "SharedRecipe" in category "SharedCategory" is NOT displayed
    And   On Back
    And   Navi to RecipesPage
    And   Recipe "SharedRecipe" in category "SharedCategory" is NOT displayed

  Scenario: Remove all Recipes
    When  Disable SnackBars
    And   Swipe to remove Recipe "SharedRecipe" in category "SharedCategory"
    And   Tap on Confirm
    And   Swipe to remove Recipe "CreatorRecipe" in category "CreatorCategory"
    And   Tap on Confirm
    Then  Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed
    And   Recipe "SharedRecipe" in category "SharedCategory" is NOT displayed
    And   No Recipes displayed
    And   On Back
    And   Navi to RecipesPage
    And   Recipe "SharedRecipe" in category "SharedCategory" is NOT displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed
    And   No Recipes displayed

  Scenario: Prevent non-creator from editing a Recipe
    When  Tap on Recipe "SharedRecipe" in category "SharedCategory"
    And   RecipeDetailsPage rendered
    And   Tap on FloatingActionButton
    Then  SaveRecipeButton is NOT displayed

  Scenario: Edit a created Recipe
    When  Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage rendered
    And   "RecipeDetailsPage" shared with "Mohammed Lee"
    And   Input "EditRecipe" as Recipe name
    And   Input "EditCategory" as Recipe category
    And   "RecipeDetailsPage" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is NOT displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Andre Option"
    And   Tap on SaveRecipeButton
    And   RecipesPage rendered
    Then  Recipe "EditRecipe" in category "EditCategory" is displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed
    And   Tap on Recipe "EditRecipe" in category "EditCategory"
    And   Recipe name is "EditRecipe"
    And   Recipe category is "EditCategory"
    And   "RecipeDetailsPage" shared with "Andre Option"
    And   On Back
    And   Navi to RecipesPage
    And   Recipe "EditRecipe" in category "EditCategory" is displayed

    #TODO  Long press add to checklist