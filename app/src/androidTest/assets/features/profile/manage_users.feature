Feature: ProfilePage User management

  Background:
    Given Init default Mocks
    And   Start on ProfilePage
    And   User "Excludie Yellow" is NOT displayed
    And   User "Mohammed Lee" is displayed
    And   User "Andre Option" is displayed

  Scenario: Connect a new User via scan
    When  Tap on AddUserButton
    And   Tap on ScanButton
    And   Tap on allow permission
    Then  ScanPage rendered
    When  Scan "Excludie@yellow.to"
    Then  ProfilePage rendered
    And   User "Excludie Yellow" is displayed

  Scenario: Connect a new User via email
    When  Tap on AddUserButton
    And   AddUserDialog is displayed
    When  Input "Excludie@yellow.to" as User email
    And   Tap on ConnectUserButton
    Then  User "Excludie Yellow" is displayed

  Scenario: Try to remove a User, but cancel confirmation
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on dismiss
    Then  User "Mohammed Lee" is displayed

  Scenario: Remove a User
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    Then  User "Mohammed Lee" is NOT displayed
