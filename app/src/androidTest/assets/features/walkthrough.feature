Feature: App walkthrough

  Background:
    Given Init Mocks
    When  Run App

  Scenario: Empty App walkthrough
    Given Tap on NaviToRegisterButton
    When  Register with first name "Bob", last name "TheDeal", email "newUser@gmx.de" and password "Password321!"
    Then  OverviewPage rendered
    And   No Checklists displayed
    When  Tap on StockButton
    Then  StockPage rendered
    And   No Stocks displayed
    When  On Back
    And   Tap on ProfileButton
    Then  ProfilePage rendered
    And   No Stocks displayed
    And   No connected Users displayed
    When  Tap on SettingsButton
    Then  SettingsPage rendered
    When  On Back
    And   On Back
    And   Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    Then  ChecklistPage rendered
    And   No Items displayed
    When  Tap on AddItemButton
    Then  SelectItemsPage rendered
    And   No Items displayed

  Scenario: StockPage create stock and manage items
    Given Mock default Users
    And   Login default User
    And   OverviewPage rendered
    And   Tap on StockButton
    And   StockPage rendered
    And   NewItemButton is NOT displayed
    When  Tap on StockPage NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Tab "NewStock" is displayed
    And   NewItemButton is displayed
    When  Tap on NewItemButton
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    When  Item "NewItem" in category "NewCategory" has amount 1.0
    And   Increase Item "NewItem" in category "NewCategory" amount by 2
    Then  Item "NewItem" in category "NewCategory" has amount 3.0

