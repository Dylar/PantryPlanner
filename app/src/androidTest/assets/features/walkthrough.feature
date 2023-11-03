Feature: App walkthrough

  Background:
    Given Init Mocks
    When  Run App

  Scenario: Empty App walkthrough
    # Register User
    Given Tap on NaviToRegisterButton
    When  Register with first name "Bob", last name "TheDeal", email "newUser@gmx.de" and password "Password321!"
    # OverviewPage
    Then  OverviewPage rendered
    And   No Checklists displayed
    And   NewChecklistButton is NOT displayed
    When  Tap on StockButton
    # StockPage
    Then  StockPage rendered
    And   No Stocks displayed
    And   On Back
    When  Tap on ProfileButton
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
    When  Tap on SettingsButton
    # SettingsPage
    Then  SettingsPage rendered
    And   On Back
    And   On Back
    # Create Checklist
    When  Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    # ChecklistPage
    Then  ChecklistPage rendered
    And   No Items displayed
    When  Tap on AddItemButton
    # SelectItemsPage
    Then  SelectItemsPage rendered
    And   No Items displayed

  Scenario: StockPage create stock, manage items then create Checklist
    Given Mock default Users
    And   Login default User
    # OverviewPage
    And   OverviewPage rendered
    And   NewChecklistButton is NOT displayed
    # StockPage
    And   Tap on StockButton
    And   StockPage rendered
    And   NewItemButton is NOT displayed
    # Create Stock
    When  Tap on StockPage NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Tab "NewStock" is displayed
    And   No Items displayed
    And   NewItemButton is displayed
    # Create Item
    When  Tap on NewItemButton
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    # Change amount
    When  Item "NewItem" in category "NewCategory" has amount 0.0
    And   Increase Item "NewItem" in category "NewCategory" amount by 4
    Then  Item "NewItem" in category "NewCategory" has amount 4.0
    # OverviewPage
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
    Then  Tap on AddItemButton
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

