Feature: StockPage Items management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Item "NewItem" in category "NewCategory" is NOT displayed

  Scenario: Render all Items and share unshared item
    Given Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
    And   Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed
    When  Tap on tab "SharedStock"
    Then  Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" has amount 0.0
    And   Item "UnsharedItem" in category "UnsharedCategory" is displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 6.66
    And   Item "UnsharedItem" in category "UnsharedCategory" unshared icon is displayed
    When  LongPress on Item "UnsharedItem" in category "UnsharedCategory"
    And   Tap on Confirm
    Then  Item "UnsharedItem" in category "UnsharedCategory" unshared icon is NOT displayed
    And   Tap on tab "CreatorStock"
    And   Item "UnsharedItem" in category "UnsharedCategory" is displayed

  Scenario Outline: Create a new Item on <StockTab>
    When  Tap on tab "<StockTab>"
    And   Tap on NewItemButton
    And   AddEditItemDialog is displayed
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   "ItemDialog" shared with "Mohammed Lee"
    And   "ItemDialog" shared with "Andre Option"
    And   "ItemDialog" unshare with "Mohammed Lee"
    And   "ItemDialog" unshare with "Andre Option"
    And   "ItemDialog" shared with none
#   TODO Change MHD and Reminder
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    And   LongPress on Item "NewItem" in category "NewCategory"
    And   Item name is "NewItem"
    And   Item category is "NewCategory"
    And   "ItemDialog" shared with none
    And   "ItemDialog" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
    And   "ItemDialog" shared with "Mohammed Lee"
    And   Tap on CreateItemButton
    Then  On Back
    And   Tap on StockButton
    And   Item "NewItem" in category "NewCategory" is displayed
    And   LongPress on Item "NewItem" in category "NewCategory"
    And   "ItemDialog" shared with "Mohammed Lee"

    Examples:
      | StockTab     |
      | CreatorStock |
      | SharedStock  |

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
    Given Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on Confirm
    And   Item "SharedItem" in category "SharedCategory" is displayed
    When  Set Item "SharedItem" in category "SharedCategory" amount to "0"
    Then  Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   On Back
    And   Tap on StockButton
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed

  Scenario: Remove all Items
    When  Disable SnackBars
    And   Set Item "SharedItem" in category "SharedCategory" amount to "0"
    And   Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on Confirm
    And   Set Item "CreatorItem" in category "CreatorCategory" amount to "0"
    And   Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on Confirm
    And   Set Item "SelectItem" in category "SelectCategory" amount to "0"
    And   Swipe to remove Item "SelectItem" in category "SelectCategory"
    And   Tap on Confirm
    Then  Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   No Items displayed
    And   On Back
    And   Tap on StockButton
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   No Items displayed

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
    When  Tap SearchBar on StockPage
    And   Input search "CreatorItem"
    Then  Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    Then  Input search "SharedItem"
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    Then  Input search "Item"
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "SelectItem" in category "SelectCategory" is displayed
    Then  Input search "NOItem"
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   No Items displayed
    Then  On Back
    And   Tap on StockButton
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "SelectItem" in category "SelectCategory" is displayed
