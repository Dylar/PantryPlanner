Feature: ChecklistPage Stock management

  Background:
    Given Init default Mocks
    And   Start on ChecklistsPage

  Scenario: Change Stock for Checklist
    Given Tap on Checklist "CreatorChecklist"
    And   Checklist Stock "CreatorStock" is displayed
    When  "ChecklistPage" open dropdown "Lager"
    And   Dropdown option "CreatorStock" is displayed
    And   Dropdown option "SharedStock" is NOT displayed
    And   Input "stock" as Checklist Stock
    And   Dropdown option "CreatorStock" is displayed
    And   Dropdown option "SharedStock" is displayed
    And   Select dropdown option "SharedStock"
    Then  Checklist Stock "SharedStock" is displayed
    And   On Back
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Checklist Stock "SharedStock" is displayed

  Scenario: Prevent non-creator from changing Stock
    Given Tap on Checklist "SharedChecklist"
    And   Checklist Stock "SharedStock" is displayed
    When  "ChecklistPage" open dropdown "Lager"
    Then  Dropdown option "CreatorStock" is NOT displayed
    And   Dropdown option "SharedStock" is NOT displayed