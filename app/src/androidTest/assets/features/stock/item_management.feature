Feature: StockPage Items management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Item "CreatorItem" should be shown
    And   Item "SharedItem" should be shown

  Scenario: Create a new Item
    When  Tap NewItemButton
    And   AddEditItemDialog is displayed
    And   Input "NewItem" as Item name
    And   Tap on CreateItemButton
    Then  Item "NewItem" should be shown
