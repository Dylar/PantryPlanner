Feature: ChecklistPage SharedChecklist Items management

  Background:
    Given Init default Mocks
    And   Start on ChecklistPage "SharedChecklist"
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" is displayed

  Scenario: Add item
    When  Tap on ChecklistPage AddItemButton
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

  Scenario: Share unshared Item
    Given Item "UnsharedItem" in category "UnsharedCategory" unshared icon is displayed
    When  LongPress on Item "UnsharedItem" in category "UnsharedCategory"
    And   Tap on Confirm
    Then  Item "UnsharedItem" in category "UnsharedCategory" unshared icon is NOT displayed