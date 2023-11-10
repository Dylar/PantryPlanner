Feature: Delete references

  Background:
    Given Init default Mocks

  Scenario: Remove Stock and check Checklist
    Given Start on OverviewPage
    And   Checklist "SharedChecklist" is displayed
    When  Tap on ProfileButton
    And   Swipe to remove Stock "SharedStock"
    And   Tap on Confirm
    And   Tap on OverviewButton
    Then  Checklist "SharedChecklist" is NOT displayed

  Scenario: Remove User and check Checklist
    Given Start on LoginPage
    #     check mohammed
    When  Login with email "mohammed@lee.to" and password "1Password!"
    Then  Tap on Checklist "SharedChecklist"
    And   "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    #     check peter
    When  Login with email "peter@lustig.to" and password "1Password!"
    And   Tap on Checklist "SharedChecklist"
    Then  "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Tap on ProfileButton
    #     remove mohammed
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    And   Tap on OverviewButton
    And   Tap on Checklist "SharedChecklist"
    #     mohammed checklist still visible
    Then  "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Tap on SettingsButton
    And   Tap on LogoutButton
    And   Tap on Confirm
    When  Login with email "mohammed@lee.to" and password "1Password!"
    #     mohammed still has the checklist
    Then  Tap on Checklist "SharedChecklist"
    And   "ChecklistPage" shared with "Peter Lustig"

  Scenario: Remove User and check Stock
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
