Feature: ProfilePage User management

  Background:
    Given Init default Mocks
    And   Start on ProfilePage
    And   User "Excludie Yellow" is NOT displayed
    And   User "Mohammed Lee" is displayed
    And   User "Andre Option" is displayed

#  Scenario: Connect a new User
#    When  Tap on ProfilePage NewStockButton
#    And   AddEditStockDialog is displayed
#    And   Input "NewStock" as Stock name
#    And   Tap on CreateStockButton
#    Then  User "NewStock" is displayed

  Scenario: Try to remove a User, but cancel confirmation
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on dismiss
    Then  User "Mohammed Lee" is displayed

  Scenario: Remove a User
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    Then  User "Mohammed Lee" is NOT displayed
