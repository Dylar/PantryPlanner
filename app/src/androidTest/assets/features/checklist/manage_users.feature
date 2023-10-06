Feature: ChecklistPage User management

  Background:
    Given Init default Mocks

  Scenario: Share with User
    Given Start on ChecklistPage "CreatorChecklist"
    And   "ChecklistPage" shared with none
    When  "ChecklistPage" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    Then  "ChecklistPage" shared with "Mohammed Lee"
    And   On back
    And   Tap on Checklist "CreatorChecklist"
    And   "ChecklistPage" shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    Given Start on ChecklistPage "SharedChecklist"
    When  "ChecklistPage" shared with "Peter Lustig"
    Then  "ChecklistPage" dropdown "Mit Benutzer teilen" is NOT displayed