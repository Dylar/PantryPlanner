Feature: App navigation

  Background:
    Given Init default Mocks

  Scenario: Run app and show LoginPage
    When  Run App
    Then  LoginPage rendered

# start on LoginPage
  Scenario: Navigate to RegisterPage
    Given Start on LoginPage
    When  Tap on NaviToRegisterButton
    Then  RegisterPage rendered

  Scenario: Navigate to ChecklistsPage
    Given Start on LoginPage
    When  Login default User
    Then  ChecklistsPage rendered

# start on ChecklistsPage
#  Scenario: Navigate to RecipePage
#    Given Start on ChecklistsPage
#    When  Navi to RecipeButton
#    Then  ProfilePage rendered

  Scenario: Navigate to StocksPage
    Given Start on ChecklistsPage
    When  Navi to StocksPage
    Then  StocksPage rendered

  Scenario: Navigate to ProfilePage
    Given Start on ChecklistsPage
    When  Navi to ProfilePage
    Then  ProfilePage rendered

  Scenario: Navigate to SettingsPage
    Given Start on ChecklistsPage
    When  Navi to SettingsPage
    Then  SettingsPage rendered

  Scenario: Navigate to ChecklistPage
    Given Start on ChecklistsPage
    When  Tap on Checklist "CreatorChecklist"
    Then  ChecklistPage rendered
