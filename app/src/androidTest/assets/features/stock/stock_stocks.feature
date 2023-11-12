Feature: StocksPage Stock management

  Background:
    Given Init default Mocks
    And   Start on StocksPage
    And   Tab "NewStock" is NOT displayed
    And   Tab "CreatorStock" is displayed
    And   Tab "SharedStock" is displayed

  Scenario: Create a new Stock
    When  Tap on StocksPage NewStockButton
    And   AddEditStockDialog is displayed
    And   Input "NewStock" as Stock name
    And   Tap on CreateStockButton
    Then  Tab "NewStock" is displayed