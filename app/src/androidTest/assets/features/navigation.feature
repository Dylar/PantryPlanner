Feature: App navigation

  Scenario: Run app and show LoginPage
    Given Init Mocks
    And   Run App
    Then  LoginPage rendered

  Scenario: Login default User and show OverviewPage
    Given Mock default User
    And   Start on LoginPage
    When  Login default User
    Then  OverviewPage rendered

  Scenario: Navigate to default User ProfilePage
    Given Mock default User
    And   Start on OverviewPage
    When  Tap on ProfileButton
    Then  ProfilePage rendered

  Scenario: Navigate to default User SettingsPage
    Given Mock default User
    And   Start on ProfilePage
    When  Tap on SettingsButton
    Then  SettingsPage rendered
