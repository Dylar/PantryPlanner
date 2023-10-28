Feature: ChecklistPage Items management

  Background:
    Given Init default Mocks
    And   Start on ChecklistPage "CreatorChecklist"
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed

  Scenario: Add item
    When  Tap on AddItemButton
    And   SelectItemsPage rendered
    Then  Tap on Item "SelectItem" in category "SelectCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    Then  ChecklistPage rendered
    And   Item "SelectItem" in category "SelectCategory" is displayed
    Then  On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SelectItem" in category "SelectCategory" is displayed

  Scenario: Remove a Item
    When  Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on Confirm
    Then  Item "CreatorItem" in category "CreatorCategory" is NOT displayed
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed

  Scenario: Remove a shared Item
    When  Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on Confirm
    Then  Item "SharedItem" in category "SharedCategory" is NOT displayed
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" is NOT displayed

  Scenario: Increase Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Increase Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 3.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 3.0

  Scenario: Decrease Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Decrease Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0

  Scenario: Increase shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Increase Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 4.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 4.0

  Scenario: Decrease shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Decrease Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 0.0
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "SharedItem" in category "SharedCategory" has amount 0.0

#  Scenario: Search Item #TODO its broken ....
#    When  Tap on AddItemButton
#    And   Tap SearchBar on SelectItemPage
#    And   Input search "CreatorItem"
#    Then  Item "CreatorItem" in category "CreatorCategory" is displayed
#    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
#    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
#    Then  Input search "SharedItem"
#    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
#    And   Item "SharedItem" in category "SharedCategory" is displayed
#    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
#    Then  Input search "Item"
#    And   Item "CreatorItem" in category "CreatorCategory" is displayed
#    And   Item "SharedItem" in category "SharedCategory" is displayed
#    And   Item "SelectItem" in category "SelectCategory" is displayed
#    Then  Input search "NOItem"
#    And   Item "CreatorItem" in category "CreatorCategory" is NOT displayed
#    And   Item "SharedItem" in category "SharedCategory" is NOT displayed
#    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
#    And   No Items displayed
#    Then  Tap on AddItemButton
#    And   On Back
#    And   Item "CreatorItem" in category "CreatorCategory" is displayed
#    And   Item "SharedItem" in category "SharedCategory" is displayed
#    And   Item "SelectItem" in category "SelectCategory" is displayed