Feature: ChecklistPage Items amount management

  Background:
    Given Init default Mocks

    #CreatorChecklist
  Scenario: Increase Item amount on CreatorChecklist
    Given Start on ChecklistPage "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Increase Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 3.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 3.0

  Scenario: Decrease Item amount on CreatorChecklist
    Given Start on ChecklistPage "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Decrease Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0

  Scenario: Increase shared Item amount on CreatorChecklist
    Given Start on ChecklistPage "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Increase Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 4.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 4.0

  Scenario: Decrease shared Item amount on CreatorChecklist
    Given Start on ChecklistPage "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Decrease Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 0.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 0.0

    #SharedChecklist
  Scenario: Increase Item amount on SharedChecklist
    Given Start on ChecklistPage "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 6.66
    When  Increase Item "UnsharedItem" in category "UnsharedCategory" amount by 2
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 8.66
    And   On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 8.66

  Scenario: Decrease Item amount on SharedChecklist
    Given Start on ChecklistPage "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 6.66
    When  Decrease Item "UnsharedItem" in category "UnsharedCategory" amount by 2
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 4.66
    And   On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 4.66
