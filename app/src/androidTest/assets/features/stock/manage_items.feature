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
    And   "ItemDialog" shared with none
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   "ItemDialog" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
#   Change MHD and Reminder
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    And   LongPress on Item "NewItem" in category "NewCategory"
    And   Item name is "NewItem"
    And   Item category is "NewCategory"
    And   "ItemDialog" shared with "Mohammed Lee"
    Then  On Back
    And   Tap on StockButton
    And   Item "NewItem" in category "NewCategory" is displayed

  Scenario: Try to remove a Item, but cancel confirmation
    When  Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on dismiss
    Then  Item "CreatorItem" in category "CreatorCategory" is displayed

  Scenario: Remove a Item
    When  Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on Confirm
    Then  Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   On Back
    And   Tap on StockButton
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed

  Scenario: Remove a shared Item
    When  Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on Confirm
    Then  Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   On Back
    And   Tap on StockButton
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed

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
    And   "ItemDialog" shared with none
    And   Input "EditItem" as Item name
    And   Input "EditCategory" as Item category
    And   "ItemDialog" open dropdown "Mit Benutzer teilen"
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
    And   "ItemDialog" shared with "Mohammed Lee"
    And   On Back
    And   Tap on StockButton
    And   Item "EditItem" in category "EditCategory" is displayed

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
    Then  On Back
    And   Tap on StockButton
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed

  Scenario: Increase Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Increase Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 3.0
    And   On Back
    And   Tap on StockButton
    And   Item "CreatorItem" in category "CreatorCategory" has amount 3.0

  Scenario: Decrease Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Decrease Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   On Back
    And   Tap on StockButton
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0

  Scenario: Increase shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.5
    When  Increase Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 4.5
    And   On Back
    And   Tap on StockButton
    And   Item "SharedItem" in category "SharedCategory" has amount 4.5

  Scenario: Decrease shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.5
    When  Decrease Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 0.5
    And   On Back
    And   Tap on StockButton
    And   Item "SharedItem" in category "SharedCategory" has amount 0.5
