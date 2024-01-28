Feature: StocksPage Items amount management

  Background:
    Given Init default Mocks
    And   Start on StocksPage
    And   Item "NewItem" in category "NewCategory" is NOT displayed
    And   Item "CreatorItem" in category "CreatorCategory" is displayed
    And   Item "SharedItem" in category "SharedCategory" is displayed

  Scenario: Prevent setting Item amount wrong value
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Set Item "CreatorItem" in category "CreatorCategory" amount to "WRONG VALUE"
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 1.0

  Scenario: Set Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Set Item "CreatorItem" in category "CreatorCategory" amount to "2"
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 2.0
    And   On Back
    And   Navi to StocksPage
    And   Item "CreatorItem" in category "CreatorCategory" has amount 2.0

  Scenario: Set shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.5
    When  Set Item "SharedItem" in category "SharedCategory" amount to "2"
    Then  Item "SharedItem" in category "SharedCategory" has amount 2.0
    And   On Back
    And   Navi to StocksPage
    And   Item "SharedItem" in category "SharedCategory" has amount 2.0

  Scenario: Increase Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Increase Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 3.0
    And   On Back
    And   Navi to StocksPage
    And   Item "CreatorItem" in category "CreatorCategory" has amount 3.0

  Scenario: Decrease Item amount
    Given Item "CreatorItem" in category "CreatorCategory" has amount 1.0
    When  Decrease Item "CreatorItem" in category "CreatorCategory" amount by 2
    Then  Item "CreatorItem" in category "CreatorCategory" has amount 0.0
    And   On Back
    And   Navi to StocksPage
    And   Item "CreatorItem" in category "CreatorCategory" has amount 0.0

  Scenario: Increase shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.5
    When  Increase Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 4.5
    And   On Back
    And   Navi to StocksPage
    And   Item "SharedItem" in category "SharedCategory" has amount 4.5

  Scenario: Decrease shared Item amount
    Given Item "SharedItem" in category "SharedCategory" has amount 2.5
    When  Decrease Item "SharedItem" in category "SharedCategory" amount by 2
    Then  Item "SharedItem" in category "SharedCategory" has amount 0.5
    And   On Back
    And   Navi to StocksPage
    And   Item "SharedItem" in category "SharedCategory" has amount 0.5
