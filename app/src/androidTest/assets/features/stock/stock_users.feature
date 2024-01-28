Feature: StocksPage User management

  Background:
    Given Init default Mocks
    And   Start on StocksPage
    And   Tab "CreatorStock" is displayed
    And   Tab "SharedStock" is displayed
#TODO load unknown user just to see them
  Scenario: Share CreatorStock with User
    Given StocksPage tap on DetailsButton
    And   "StockPage CreatorStock" shared with none
    When  "StockPage CreatorStock" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
    And   On Back
    And   On Back
    And   Navi to StocksPage
    And   StocksPage tap on DetailsButton
    And   "StockPage CreatorStock" shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    When  Tap on tab "SharedStock"
    And   StocksPage "SharedStock" details is NOT rendered
    And   StocksPage tap on DetailsButton
    And   StocksPage "SharedStock" details is rendered
    Then  "StockPage SharedStock" shared with "Peter Lustig"
    And   "StockPage SharedStock" dropdown "Mit Benutzer teilen" is NOT displayed