Feature: ChecklistPage User management

  Background:
    Given Init default Mocks

  Scenario: Share with User
    Given Start on ChecklistPage "CreatorChecklist"
    And   Shared with none
    When  Open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    Then  Shared with "Mohammed Lee"
    And   On back
    And   Tap on Checklist "CreatorChecklist"
    And   Shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    Given Start on ChecklistPage "SharedChecklist"
    When  Shared with "Peter Lustig"
    Then  Dropdown "Mit Benutzer teilen" is NOT displayed