Feature: StockPage Items management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Item "NewItem" is NOT displayed
    And   Item "CreatorItem" is displayed
    And   Item "SharedItem" is displayed

  Scenario: Create a new Item
    When  Tap on NewItemButton
    And   AddEditItemDialog is displayed
    And   Input "NewItem" as Item name
    And   Tap on CreateItemButton
    Then  Item "NewItem" is displayed

  Scenario: Try to remove a Item, but cancel confirmation
    When  Swipe to remove Item "CreatorItem"
    And   Tap on dismiss
    Then  Item "CreatorItem" is displayed

  Scenario: Remove a Item
    When  Swipe to remove Item "CreatorItem"
    And   Tap on confirm
    Then  Item "CreatorItem" is NOT displayed

  Scenario: Remove a shared Item
    When  Swipe to remove Item "SharedItem"
    And   Tap on confirm
    Then  Item "SharedItem" is NOT displayed

  Scenario: Prevent non-creator from editing a Item
    When  LongPress on Item "SharedItem"
    And   AddEditItemDialog is displayed
    And   Input "EditItem" as Item name
    And   Tap on CreateItemButton
    Then  Item "EditItem" is NOT displayed
    And   Item "SharedItem" is displayed
    And   SnackBar shown: "Nur der Ersteller kann das Item ändern"

  Scenario: Edit a created Item
    When  LongPress on Item "CreatorItem"
    And   AddEditItemDialog is displayed
    And   Input "EditItem" as Item name
    And   Tap on CreateItemButton
    Then  Item "EditItem" is displayed
    And   Item "CreatorStock" is NOT displayed

  Scenario: Share Item with User
    Given  LongPress on Item "CreatorItem"
    And    Shared with none
    When   Open dropdown "Mit Benutzer teilen"
    And    Dropdown option "Mohammed Lee" is displayed
    And    Dropdown option "Andre Option" is displayed
    And    Select dropdown option "Mohammed Lee"
    And    Tap on CreateItemButton
    Then   LongPress on Item "CreatorItem"
    And    Shared with "Mohammed Lee"
