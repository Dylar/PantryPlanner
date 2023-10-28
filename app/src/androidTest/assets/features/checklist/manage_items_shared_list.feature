Feature: ChecklistPage Items management

  Background:
    Given Init default Mocks
    And   Start on ChecklistPage "SharedChecklist"
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" is displayed

  Scenario: Add item
    When  Tap on AddItemButton
    And   Tap on Item "SelectItem" in category "SelectCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    Then  Item "SelectItem" in category "SelectCategory" is displayed
    Then  On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "SelectItem" in category "SelectCategory" is displayed

  Scenario: Remove a Item
    When  Swipe to remove Item "UnsharedItem" in category "UnsharedCategory"
    And   Tap on Confirm
    Then  Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed
    And   On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed

  Scenario: Increase Item amount
    Given Item "UnsharedItem" in category "UnsharedCategory" has amount 6.66
    When  Increase Item "UnsharedItem" in category "UnsharedCategory" amount by 2
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 8.66
    And   On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 8.66

  Scenario: Decrease Item amount
    Given Item "UnsharedItem" in category "UnsharedCategory" has amount 6.66
    When  Decrease Item "UnsharedItem" in category "UnsharedCategory" amount by 2
    Then  Item "UnsharedItem" in category "UnsharedCategory" has amount 4.66
    And   On Back
    And   Tap on Checklist "SharedChecklist"
    And   Item "UnsharedItem" in category "UnsharedCategory" has amount 4.66
