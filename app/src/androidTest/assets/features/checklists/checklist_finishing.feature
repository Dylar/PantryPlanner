Feature: ChecklistPage Un/Finish checklist

  Background:
    Given Init default Mocks
    And   Start on StocksPage

  Scenario: Finish Checklist and add checked Item amount to Stock
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Tap on FinishButton
    And   Tap on Confirm
    And   Navi to StocksPage
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 2.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5

  Scenario: Check Items and finish Checklist and add checked Item amount to Stock
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
    And   On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0
    When  Tap on Item "CreatorItem" in category "CreatorCategory"
    And   Tap on Item "SharedItem" in category "SharedCategory"
    And   Tap on FinishButton
    And   Tap on Confirm
    And   Navi to StocksPage
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 4.5

  Scenario: Prevent non-creator from finish Checklist
    Given On Back
    And   Tap on Checklist "SharedChecklist"
    When  Tap on FinishButton
    And   Tap on Confirm
    Then  SnackBar shown: "Du hast die Liste nicht erstellt"

  Scenario: Prevent empty Checklist from finishing
    Given On Back
    And   Tap on Checklist "CreatorChecklist"
    And   Swipe to remove Item "CreatorItem" in category "CreatorCategory"
    And   Tap on Confirm
    And   Swipe to remove Item "SharedItem" in category "SharedCategory"
    And   Tap on Confirm
    And   No Items displayed
    When  Tap on FinishButton
    And   Tap on Confirm
    Then  SnackBar shown: "Liste enthält keine Items"

  Scenario: Unfinish Checklist and reduce checked Item amount to Stock
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5
    And   On Back
    When  Tap on Checklist "FinishedChecklist"
    And   Tap on Confirm
    And   Navi to StocksPage
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   Item "SharedItem" in category "SharedCategory" has amount 2.5