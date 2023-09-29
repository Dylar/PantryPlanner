Feature: ProfilePage Stock management

  Background:
    Given Init default Mocks
    And   Start on ProfilePage
    And   Stock "Home" should NOT be shown
    And   Stock "CreatorStock" should be shown
    And   Stock "SharedStock" should be shown

  Scenario: Create a new Stock
    When  Tap NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "Home" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "Home" should be shown

  Scenario: Try to remove a Stock, but cancel confirmation
    When  Swipe to remove Stock "CreatorStock"
    And   Tap on dismiss
    Then  Stock "CreatorStock" should be shown

  Scenario: Remove a Stock
    When  Swipe to remove Stock "CreatorStock"
    And   Tap on confirm
    Then  Stock "CreatorStock" should NOT be shown

  Scenario: Remove a shared Stock
    When  Swipe to remove Stock "SharedStock"
    And   Tap on confirm
    Then  Stock "SharedStock" should NOT be shown

  Scenario: Prevent non-creator from editing a Stock
    When  LongPress on Stock "SharedStock"
    And   AddEditStockDialog is displayed
    And   Input "Home" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "Home" should NOT be shown
    And   Stock "SharedStock" should be shown
    And   SnackBar shown: "Nur der Ersteller kann den Ort ändern"

  Scenario: Edit a created Stock
    When  LongPress on Stock "CreatorStock"
    And   AddEditStockDialog is displayed
    And   Input "Home" as Stock name
    And   Tap on CreateStockButton
    Then  Stock "Home" should be shown
    And   Stock "CreatorStock" should NOT be shown

  Scenario: Share Stock with User
    Given  LongPress on Stock "CreatorStock"
    And    Shared with none
    When   Open dropdown
    And    Dropdown option "Mohammed Lee" is visible
    And    Dropdown option "Andre Option" is visible
    And    Select dropdown option "Mohammed Lee"
    And    Tap on CreateStockButton
    Then   LongPress on Stock "CreatorStock"
    And    Shared with "Mohammed Lee"
