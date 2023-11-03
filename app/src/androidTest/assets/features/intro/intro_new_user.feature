Feature: Intro Create new User

  Background:
    Given Init default Mocks
    And   Start on LoginPage

  Scenario: User not registered -> login fails
    When  Login with email "newUser@gmx.de" and password "Password321!"
    Then  SnackBar shown: "Benutzer nicht gefunden"

  Scenario: Register new User, logout and login
    When  Tap on NaviToRegisterButton
    And   Register with first name "Bob", last name "TheDeal", email "newUser@gmx.de" and password "Password321!"
    Then  OverviewPage rendered
    And   Tap on ProfileButton
    And   Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    Then  LoginPage rendered
    And   Login with email "newUser@gmx.de" and password "Password321!"
    And   OverviewPage rendered