Feature: App walkthrough

  Background:
    Given Init Mocks
    When  Run App

  Scenario: Empty App walkthrough
    # Register User
    Given Tap on NaviToRegisterButton
    When  Register with first name "Bob", last name "TheDeal", email "newUser@gmx.de" and password "Password321!"
    # ChecklistsPage
    Then  ChecklistsPage rendered
    And   No Checklists displayed
    And   NewChecklistButton is NOT displayed
    When  Navi to StocksPage
    # StocksPage
    Then  StocksPage rendered
    And   No Stocks displayed
    And   On Back
    When  Navi to ProfilePage
    # ProfilePage
    Then  ProfilePage rendered
    And   No Stocks displayed
    And   No connected Users displayed
    # Create Stock
    When  Tap on ProfilePage NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "NewStock" is displayed
    When  Navi to SettingsPage
    # SettingsPage
    Then  SettingsPage rendered
    And   On Back
    # Create Checklist
    When  Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    # ChecklistPage
    Then  ChecklistPage rendered
    And   No Items displayed
    When  Tap on ChecklistPage AddItemButton
    # SelectItemsPage
    Then  SelectItemsPage rendered
    And   No Items displayed

  Scenario: StocksPage create stock, manage items then create Checklist
    Given Mock default Users
    And   Login default User
    # ChecklistsPage
    And   ChecklistsPage rendered
    And   NewChecklistButton is NOT displayed
    # StocksPage
    And   Navi to StocksPage
    And   StocksPage rendered
    And   NewItemButton is NOT displayed
    # Create Stock
    When  Tap on StocksPage NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Tab "NewStock" is displayed
    And   No Items displayed
    And   NewItemButton is displayed
    # Create Item
    When  Tap on StocksPage NewItemButton
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    # Change amount
    When  Item "NewItem" in category "NewCategory" has amount 0.0
    And   Increase Item "NewItem" in category "NewCategory" amount by 4
    Then  Item "NewItem" in category "NewCategory" has amount 4.0
    # ChecklistsPage
    When  On Back
    Then  NewChecklistButton is displayed
    # Create Checklist
    And   Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    # ChecklistPage
    When  ChecklistPage rendered
    And   No Items displayed
    Then  Tap on ChecklistPage AddItemButton
    # SelectItemsPage
    When  SelectItemsPage rendered
    And   Item "NewItem" in category "NewCategory" is displayed
    And   Tap on Item "NewItem" in category "NewCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    # ChecklistPage
    When  ChecklistPage rendered
    And   Item "NewItem" in category "NewCategory" is displayed
    And   Item "NewItem" in category "NewCategory" has amount 1.0
    Then  Increase Item "NewItem" in category "NewCategory" amount by 4
    And   Item "NewItem" in category "NewCategory" has amount 5.0

