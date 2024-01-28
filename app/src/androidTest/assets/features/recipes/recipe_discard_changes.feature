Feature: Discard Recipe changes

  Background:
    Given Init default Mocks
    And   Start on RecipesPage
    And   Recipe "NewRecipe" in category "NewCategory" is NOT displayed
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is displayed
    And   Recipe "SharedRecipe" in category "SharedCategory" is displayed

  Scenario: Create Recipe, but discard it
    Given Tap on NewRecipeButton
    And   RecipeDetailsPage details is NOT rendered
    And   RecipeDetailsPage tap on DetailsButton
    And   RecipeDetailsPage details is rendered
    And   Input "NewRecipe" as Recipe name
    And   Input "NewCategory" as Recipe category
    When  On Back
    And   On Back
    And   Tap on dismiss
    Then  RecipesPage rendered
    And   Recipe "NewRecipe" in category "NewCategory" is NOT displayed

  Scenario: Create Recipe, do nothing and show no discard dialog
    Given Tap on NewRecipeButton
    When  On Back
    Then  RecipesPage rendered

  Scenario: Edit nothing and show no discard dialog
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    When  On Back
    Then  RecipesPage rendered

  Scenario: Edit Recipe name, but discard it
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage tap on DetailsButton
    And   Input "EditRecipe" as Recipe name
    When  On Back
    When  On Back
    And   Tap on dismiss
    Then  RecipesPage rendered
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is displayed
    And   Recipe "EditRecipe" in category "CreatorCategory" is NOT displayed

  Scenario: Edit Recipe category, but discard it
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage tap on DetailsButton
    And   Input "EditCategory" as Recipe category
    When  On Back
    When  On Back
    And   Tap on dismiss
    Then  RecipesPage rendered
    And   Recipe "CreatorRecipe" in category "CreatorCategory" is displayed
    And   Recipe "CreatorRecipe" in category "EditCategory" is NOT displayed

  Scenario: Edit Recipe shared User, but discard it
    Given Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage tap on DetailsButton
    And   "RecipeDetailsPage" shared with "Mohammed Lee"
    And   "RecipeDetailsPage" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Andre Option"
    And   "RecipeDetailsPage" shared with "Andre Option"
    When  On Back
    When  On Back
    And   Tap on dismiss
    Then  RecipesPage rendered
    And   Tap on Recipe "CreatorRecipe" in category "CreatorCategory"
    And   RecipeDetailsPage tap on DetailsButton
    And   "RecipeDetailsPage" shared with "Mohammed Lee"