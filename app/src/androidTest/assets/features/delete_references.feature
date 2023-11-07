Feature: Delete references

  Background:
    Given Init default Mocks

  Scenario: Remove Stock reference
    Given Start on OverviewPage
    And   Checklist "SharedChecklist" is displayed
    When  Tap on ProfileButton
    And   Swipe to remove Stock "SharedStock"
    And   Tap on Confirm
    And   Tap on OverviewButton
    Then  Checklist "SharedChecklist" is NOT displayed

  Scenario: Remove User reference and check Checklist
    Given Start on LoginPage
    #     check mohammed
    When  Login with email "mohammed@lee.to" and password "1Password!"
    Then  Checklist "CreatorChecklist" is displayed
    And   Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    #     check peter
    When  Login with email "peter@lustig.to" and password "1Password!"
    And   Tap on Checklist "CreatorChecklist"
    Then  "ChecklistPage" shared with "Mohammed Lee"
    And   On Back
    And   Tap on ProfileButton
    #     remove mohammed
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    And   Tap on OverviewButton
    And   Tap on Checklist "CreatorChecklist"
    #     mohammed still visible
    Then  "ChecklistPage" shared with "Mohammed Lee"
    And   On Back
    And   Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    When  Login with email "mohammed@lee.to" and password "1Password!"
    #     mohammed still has the checklist
    Then  Checklist "CreatorChecklist" is displayed

  Scenario: Remove User reference and check Stock
    Given Start on OverviewPage
    When  Tap on StockButton
    And   "StockPage CreatorStock" shared with none
    And   "StockPage CreatorStock" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
    And   Tap on ProfileButton
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    And   Tap on StockButton
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
