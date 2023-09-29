Feature: App navigation

  Background:
    Given Init default Mocks

  Scenario: Run app and show LoginPage
    When  Run App
    Then  LoginPage rendered

# start on LoginPage
  Scenario: Navigate to RegisterPage
    Given Start on LoginPage
    When  Tap on RegisterButton
    Then  RegisterPage rendered

  Scenario: Navigate to OverviewPage
    Given Start on LoginPage
    When  Login default User
    Then  OverviewPage rendered

# start on OverviewPage
  Scenario: Navigate to StockPage
    Given Start on OverviewPage
    When  Tap on StockButton
    Then  StockPage rendered

  Scenario: Navigate to ProfilePage
    Given Start on OverviewPage
    When  Tap on ProfileButton
    Then  ProfilePage rendered

# start on ProfilePage
  Scenario: Navigate to SettingsPage
    Given Start on ProfilePage
    When  Tap on SettingsButton
    Then  SettingsPage rendered
