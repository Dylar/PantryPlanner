Feature: StockPage User management

  Background:
    Given Init default Mocks
    And   Start on StockPage
    And   Tab "CreatorStock" is displayed
    And   Tab "SharedStock" is displayed

  Scenario: Share CreatorStock with User
    Given Shared with none
    When  Open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    Then  Shared with "Mohammed Lee"
    And   On back
    And   Tap on StockButton
    And   Shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    When  Tap on tab "SharedStock"
    Then  Shared with "Peter Lustig"
    And   Dropdown "Mit Benutzer teilen" is NOT displayed