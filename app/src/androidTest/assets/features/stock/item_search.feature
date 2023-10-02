Feature: StockPage Item search

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed

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