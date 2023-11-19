Feature: ChecklistPage CreatorChecklist Items management

  Background:
    Given Init default Mocks
    And   Start on ChecklistPage "CreatorChecklist"
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed
    And   Item "UnsharedItem" in category "UnsharedCategory" is NOT displayed

  Scenario: Create Item and add it
    When  Tap on ChecklistPage AddItemButton
    And   SelectItemsPage rendered
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is displayed
    # create item
    When  Tap on SelectItemsPage NewItemButton
    And   Input "NewItem" as Item name
    And   Input "NewCategory" as Item category
    And   Tap on CreateItemButton
    Then  Item "NewItem" in category "NewCategory" is displayed
    # select only non-created item
    When  Tap on Item "SelectItem" in category "SelectCategory"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    Then  ChecklistPage rendered
    # only selected item visible
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "SelectItem" in category "SelectCategory" is displayed
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "NewItem" in category "NewCategory" is NOT displayed
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