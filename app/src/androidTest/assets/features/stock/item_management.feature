Feature: StockPage Items management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed

  Scenario: Create a new Item
    When  Tap on NewItemButton
    And   AddEditItemDialog is displayed
    And   Shared with none
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   Open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
#   Change MHD and Reminder
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    And   LongPress on Item "NewItem" in category "NewCategory"
    And   Item name is "NewItem"
    And   Item category is "NewCategory"
    And   Shared with "Mohammed Lee"

  Scenario: Try to remove a Item, but cancel confirmation
    When  Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on dismiss
    Then  Item "CreatorItem" in category "CreatorCategory" is displayed

  Scenario: Remove a Item
    When  Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on confirm
    Then  Item "CreatorItem" in category "CreatorCategory" is NOT displayed

  Scenario: Remove a shared Item
    When  Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on confirm
    Then  Item "SharedItem" in category "SharedCategory" is NOT displayed

  Scenario: Prevent non-creator from editing a Item
    When  LongPress on Item "SharedItem" in category "SharedCategory"
    And   AddEditItemDialog is displayed
    And   Input "EditItem" as Item name
    And   Tap on CreateItemButton
    Then  Item "EditItem" in category "EditCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   SnackBar shown: "Nur der Ersteller kann das Item ändern"

  Scenario: Edit a created Item
    When  LongPress on Item "CreatorItem" in category "CreatorCategory"
    And   AddEditItemDialog is displayed
    And   Shared with none
    And   Input "EditItem" as Item name
    And   Input "EditCategory" as Item category
    And   Open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
#   Change MHD and Reminder
    And   Tap on CreateItemButton
    Then  Item "EditItem" in category "EditCategory" is displayed
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   LongPress on Item "EditItem" in category "EditCategory"
    And   Item name is "EditItem"
    And   Item category is "EditCategory"
    And   Shared with "Mohammed Lee"

  Scenario: Search Item
    When  Tap on SearchBar
    And   Input search "CreatorItem"
    Then  Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    Then  Input search "SharedItem"
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    Then  Input search "Item"
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    Then  Input search "NOItem"
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   No Items displayed