Feature: ProfilePage Stock management

  Background:
    Given Init default Mocks
    And   Start on ProfilePage
    And   Stock "NewStock" is NOT displayed
    And   Stock "CreatorStock" is displayed
    And   Stock "SharedStock" is displayed

  Scenario: Create a new Stock
    When  Tap on NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "NewStock" is displayed

  Scenario: Try to remove a Stock, but cancel confirmation
    When  Swipe to remove Stock "CreatorStock"
    And   Tap on dismiss
    Then  Stock "CreatorStock" is displayed

  Scenario: Remove a Stock
    When  Swipe to remove Stock "CreatorStock"
    And   Tap on confirm
    Then  Stock "CreatorStock" is NOT displayed

  Scenario: Remove a shared Stock
    When  Swipe to remove Stock "SharedStock"
    And   Tap on confirm
    Then  Stock "SharedStock" is NOT displayed

  Scenario: Prevent non-creator from editing a Stock
    When  LongPress on Stock "SharedStock"
    And   AddEditStockDialog is displayed
    And   Input "EditStock" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "EditStock" is NOT displayed
    And   Stock "SharedStock" is displayed
    And   SnackBar shown: "Nur der Ersteller kann den Ort ändern"

  Scenario: Edit a created Stock
    When  LongPress on Stock "CreatorStock"
    And   AddEditStockDialog is displayed
    And   Input "EditStock" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "EditStock" is displayed
    And   Stock "CreatorStock" is NOT displayed

  Scenario: Share Stock with User
    Given  LongPress on Stock "CreatorStock"
    And    Shared with none
    When   Open dropdown "Mit Benutzer teilen"
    And    Dropdown option "Mohammed Lee" is displayed
    And    Dropdown option "Andre Option" is displayed
    And    Select dropdown option "Mohammed Lee"
    And    Tap on CreateStockButton
    Then   LongPress on Stock "CreatorStock"
    And    Shared with "Mohammed Lee"
