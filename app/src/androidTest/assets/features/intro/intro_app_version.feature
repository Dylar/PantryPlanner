Feature: Intro new app version

  Scenario: Run App -> new minor version -> show new app version dialog
    Given Init Mocks
    And   Mock App Version "0.1"
    When  Run App
    Then  NewAppVersionDialog is displayed
    When  Tap on cancel NewAppVersionDialog
    Then  LoginPage rendered

  Scenario: Run App -> new major version -> show new app version dialog
    Given Init Mocks
    And   Mock App Version "1.0"
    When  Run App
    Then  NewAppVersionDialog is displayed
    When  Tap on cancel NewAppVersionDialog
    Then  LoginPage rendered

  Scenario: Run App -> old version -> don't show new app version dialog
    Given Init Mocks
      # haha
    And   Mock App Version "0.-1"
      # haha
    When  Run App
    Then  LoginPage rendered
