Feature: OverviewPage Checklist management

  Background:
    Given Init default Mocks
    And   Start on OverviewPage
    And   Checklist "NewChecklist" is NOT displayed
    And   Checklist "CreatorChecklist" is displayed
    And   Checklist "SharedChecklist" is displayed

  Scenario: Create a new Checklist
    When  Tap on NewChecklistButton
    And   AddEditChecklistDialog is displayed
    And   "ChecklistDialog" shared with none
    And   Input "NewChecklist" as Checklist name
    And   "ChecklistDialog" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
    And   Tap on CreateChecklistButton
    Then  Checklist "NewChecklist" is displayed
    And   LongPress on Checklist "NewChecklist"
    And   Checklist name is "NewChecklist"
    And   "ChecklistDialog" shared with "Mohammed Lee"

  Scenario: Try to remove a Checklist, but cancel confirmation
    When  Swipe to remove Checklist "CreatorChecklist"
    And   Tap on dismiss
    Then  Checklist "CreatorChecklist" is displayed

  Scenario: Remove a Checklist
    When  Swipe to remove Checklist "CreatorChecklist"
    And   Tap on confirm
    Then  Checklist "CreatorChecklist" is NOT displayed

  Scenario: Remove a shared Checklist
    When  Swipe to remove Checklist "SharedChecklist"
    And   Tap on confirm
    Then  Checklist "SharedChecklist" is NOT displayed

  Scenario: Prevent non-creator from editing a Checklist
    When  LongPress on Checklist "SharedChecklist"
    And   AddEditChecklistDialog is displayed
    And   Input "EditChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    Then  Checklist "EditChecklist" is NOT displayed
    And   Checklist "SharedChecklist" is displayed
    And   SnackBar shown: "Nur der Ersteller kann die Checklist ändern"

  Scenario: Edit a created Checklist
    When  LongPress on Checklist "CreatorChecklist"
    And   AddEditChecklistDialog is displayed
    And   "ChecklistDialog" shared with none
    And   Input "EditChecklist" as Checklist name
    And   "ChecklistDialog" open dropdown "Mit Benutzer teilen"
    And   Dropdown option "Mohammed Lee" is displayed
    And   Dropdown option "Andre Option" is displayed
    And   Select dropdown option "Mohammed Lee"
    And   Tap on CreateChecklistButton
    Then  Checklist "EditChecklist" is displayed
    And   Checklist "CreatorChecklist" is NOT displayed
    And   LongPress on Checklist "EditChecklist"
    And   Checklist name is "EditChecklist"
    And   "ChecklistDialog" shared with "Mohammed Lee"