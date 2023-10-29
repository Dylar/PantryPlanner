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

  Scenario Outline: Connect a new User via <Method> and see if other user is connected too
    # logout and login other user
    Given Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    And   Login with email "excludie@yellow.to" and password "1Password!"
    And   Tap on ProfileButton
    And   User "Peter Lustig" is NOT displayed
    And   User "Mohammed Lee" is NOT displayed
    And   User "Andre Option" is NOT displayed
  # connect user
    When  Tap on AddUserButton
    And   Connect User "peter@lustig.to" via <Method>
    And   User "Peter Lustig" is displayed
  # logout and login user
    Then  Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    And   Login with email "peter@lustig.to" and password "1Password!"
    And   Tap on ProfileButton
    And   User "Excludie Yellow" is displayed

    Examples:
      | Method |
      | Email  |
      | Scan   |

