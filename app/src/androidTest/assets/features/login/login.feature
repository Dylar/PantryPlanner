Feature: App navigation

  Scenario: Run app and show LoginPage
    Given Mock nothing
    And   Run App
    Then  Login page rendered

  Scenario: Login default user and show OverviewPage
    Given Mock default User
    And   Start on LoginPage
    When  Login default user
    Then  Overview page rendered
