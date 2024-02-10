Feature: ChecklistPage duplicate bug

  Scenario: Add more than 10 Items - show no duplicates
    Given Init default Mocks
    And   Mock 12 Items
    And   Start on ChecklistPage "CreatorChecklist"
    And   Tap on ChecklistPage AddItemButton
    And   Tap on Item "NewItem 1" in category "NewCategory 1"
    And   Tap on Item "NewItem 10" in category "NewCategory 10"
    And   Tap on Item "NewItem 2" in category "NewCategory 2"
    And   Tap on Item "NewItem 3" in category "NewCategory 3"
    And   Tap on Item "NewItem 4" in category "NewCategory 4"
    And   Scroll to index 10
    And   Tap on Item "NewItem 5" in category "NewCategory 5"
    And   Tap on Item "NewItem 6" in category "NewCategory 6"
    And   Tap on Item "NewItem 7" in category "NewCategory 7"
    And   Tap on Item "NewItem 8" in category "NewCategory 8"
    And   Tap on Item "NewItem 9" in category "NewCategory 9"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    And   Item "NewItem 1" in category "NewCategory 1" is displayed
    And   Item "NewItem 10" in category "NewCategory 10" is displayed
    And   Item "NewItem 11" in category "NewCategory 11" is NOT displayed
    And   Item "NewItem 2" in category "NewCategory 2" is displayed
    And   Item "NewItem 3" in category "NewCategory 3" is displayed
    And   Item "NewItem 4" in category "NewCategory 4" is displayed
    And   Scroll to index 10
    And   Item "NewItem 5" in category "NewCategory 5" is displayed
    And   Item "NewItem 6" in category "NewCategory 6" is displayed
    And   Item "NewItem 7" in category "NewCategory 7" is displayed
    And   Item "NewItem 8" in category "NewCategory 8" is displayed
    And   Item "NewItem 9" in category "NewCategory 9" is displayed
    When  Tap on ChecklistPage AddItemButton
    And   Tap on Item "NewItem 11" in category "NewCategory 11"
    And   Tap on AddSelectionButton
    And   Tap on Confirm
    And   Scroll to index 0
    Then  Item "NewItem 1" in category "NewCategory 1" is displayed
    And   Item "NewItem 10" in category "NewCategory 10" is displayed
    And   Item "NewItem 11" in category "NewCategory 11" is displayed
    And   Item "NewItem 2" in category "NewCategory 2" is displayed
    And   Item "NewItem 3" in category "NewCategory 3" is displayed
    And   Scroll to index 12
    And   Item "NewItem 4" in category "NewCategory 4" is displayed
    And   Item "NewItem 5" in category "NewCategory 5" is displayed
    And   Item "NewItem 6" in category "NewCategory 6" is displayed
    And   Item "NewItem 7" in category "NewCategory 7" is displayed
    And   Item "NewItem 8" in category "NewCategory 8" is displayed
    And   Scroll to index 12
    And   Item "NewItem 9" in category "NewCategory 9" is displayed