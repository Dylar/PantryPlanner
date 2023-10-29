Feature: ChecklistPage Items CreatorChecklist management

  Background:
    Given Init default Mocks
    And   Start on ChecklistPage "CreatorChecklist"
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed

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