Feature: Intro new app version

  Scenario: Run App -> show new app version dialog
    Given Init Mocks
    And   Mock App Version "1.0.1"
    When  Run App
    Then  NewAppVersionDialog is displayed
    When  Tap on cancel NewAppVersionDialog
    Then  LoginPage rendered
