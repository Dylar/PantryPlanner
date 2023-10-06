Feature: StockPage User management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Tab "CreatorStock" is displayed
    And   Tab "SharedStock" is displayed

  Scenario: Share CreatorStock with User
    Given "StockPage CreatorStock" shared with none
    When  "StockPage CreatorStock" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
    And   On back
    And   Tap on StockButton
    And   "StockPage CreatorStock" shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    When  Tap on tab "SharedStock"
    Then  "StockPage SharedStock" shared with "Peter Lustig"
    And   "StockPage SharedStock" dropdown "Mit Benutzer teilen" is NOT displayed