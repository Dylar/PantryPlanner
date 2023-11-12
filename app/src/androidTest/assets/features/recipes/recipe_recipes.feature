Feature: RecipesPage Recipe management

  Background:
    Given Init default Mocks
    And   Start on RecipesPage
    And   Recipe "NewRecipe" in category "NewCategory" is NOT displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is displayed
    And   Recipe "SharedRecipe" in category "SharedCategory" is displayed

# TODO filter for cookable

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
#   TODO add items
    And   Tap on SaveRecipeButton
    Then  RecipesPage rendered
    And   Recipe "NewRecipe" in category "NewCategory" is displayed
    And   LongPress on Recipe "NewRecipe" in category "NewCategory"
    And   RecipeDetailsPage rendered
    And   Recipe name is "NewRecipe"
    And   Recipe category is "NewCategory"
    And   "RecipeDetailsPage" shared with "Mohammed Lee"
#   TODO check items

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
    And   Recipe "SharedRecipe" in category "SharedCategory" is displayed
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
    When  LongPress on Recipe "SharedRecipe" in category "SharedCategory"
    And   RecipeDetailsPage rendered
    And   Input "EditRecipe" as Recipe name
    And   Recipe name is "SharedRecipe"
    And   Input "EditCategory" as Recipe category
    And   Recipe category is "SharedCategory"
    And   Tap on SaveRecipeButton
    And   RecipeDetailsPage rendered
    And   On Back
    Then  Recipe "EditRecipe" in category "EditCategory" is NOT displayed
    And   Recipe "SharedRecipe" in category "SharedCategory" is displayed

  Scenario: Edit a created Recipe
    When  LongPress on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage rendered
    And   "RecipeDetailsPage" shared with none
    And   Input "EditRecipe" as Recipe name
    And   Input "EditCategory" as Recipe category
    And   "RecipeDetailsPage" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    And   Tap on SaveRecipeButton
    And   RecipesPage rendered
    Then  Recipe "EditRecipe" in category "EditCategory" is displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is NOT displayed
    And   LongPress on Recipe "EditRecipe" in category "EditCategory"
    And   Recipe name is "EditRecipe"
    And   Recipe category is "EditCategory"
    And   "RecipeDetailsPage" shared with "Mohammed Lee"
    And   On Back
    And   Navi to RecipesPage
    And   Recipe "EditRecipe" in category "EditCategory" is displayed
