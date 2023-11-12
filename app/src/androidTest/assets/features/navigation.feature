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

  Scenario: Navigate to OverviewPage
    Given Start on LoginPage
    When  Login default User
    Then  OverviewPage rendered

# start on OverviewPage
  Scenario: Navigate to RecipePage
    Given Start on OverviewPage
    When  Navi to RecipeButton
    Then  ProfilePage rendered

  Scenario: Navigate to StockPage
    Given Start on OverviewPage
    When  Navi to StockPage
    Then  StockPage rendered

  Scenario: Navigate to ProfilePage
    Given Start on OverviewPage
    When  Navi to ProfilePage
    Then  ProfilePage rendered

  Scenario: Navigate to SettingsPage
    Given Start on OverviewPage
    When  Navi to SettingsPage
    Then  SettingsPage rendered

  Scenario: Navigate to ChecklistPage
    Given Start on OverviewPage
    When  Tap on Checklist "CreatorChecklist"
    Then  ChecklistPage rendered
