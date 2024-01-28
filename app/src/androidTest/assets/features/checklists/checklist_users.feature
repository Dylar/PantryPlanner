Feature: ChecklistPage User management

  Background:
    Given Init default Mocks

  Scenario: Share with User
    Given Start on ChecklistPage "CreatorChecklist"
    And   ChecklistPage tap on DetailsButton
    And   "ChecklistPage" shared with "Mohammed Lee"
    And   "ChecklistPage" unshare with "Mohammed Lee"
    Then  "ChecklistPage" shared with none
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   ChecklistPage tap on DetailsButton
    And   "ChecklistPage" shared with none
    When  "ChecklistPage" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    And   "ChecklistPage" shared with "Mohammed Lee"
    And   On Back
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   ChecklistPage tap on DetailsButton
    And   "ChecklistPage" shared with "Mohammed Lee"

  Scenario: Prevent non-creator from sharing with User
    Given Start on ChecklistPage "SharedChecklist"
    And   ChecklistPage details is NOT rendered
    And   ChecklistPage tap on DetailsButton
    And   ChecklistPage details is rendered
    When  "ChecklistPage" shared with "Peter Lustig"
    Then  "ChecklistPage" dropdown "Mit Benutzer teilen" is NOT displayed
