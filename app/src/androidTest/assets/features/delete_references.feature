Feature: Delete references

  Background:
    Given Init default Mocks

  Scenario: Remove Stock and check Checklist
    Given Start on OverviewPage
    And   Checklist "SharedChecklist" is displayed
    When  Navi to ProfilePage
    And   Swipe to remove Stock "SharedStock"
    And   Tap on Confirm
    And   Navi to OverviewButton
    Then  Checklist "SharedChecklist" is NOT displayed

  Scenario: Remove User and check Checklist
    Given Start on LoginPage
    #     check mohammed
    When  Login with email "mohammed@lee.to" and password "1Password!"
    Then  Tap on Checklist "SharedChecklist"
    And   "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Navi to SettingsPage
    And   Tap on LogoutButton
    And   Tap on Confirm
    #     check peter
    When  Login with email "peter@lustig.to" and password "1Password!"
    And   Tap on Checklist "SharedChecklist"
    Then  "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Navi to ProfilePage
    #     remove mohammed
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    And   Navi to OverviewButton
    And   Tap on Checklist "SharedChecklist"
    #     mohammed checklist still visible
    Then  "ChecklistPage" shared with "Peter Lustig"
    And   On Back
    And   Navi to SettingsPage
    And   Tap on LogoutButton
    And   Tap on Confirm
    When  Login with email "mohammed@lee.to" and password "1Password!"
    #     mohammed still has the checklist
    Then  Tap on Checklist "SharedChecklist"
    And   "ChecklistPage" shared with "Peter Lustig"

  Scenario: Remove User and check Stock
    Given Start on OverviewPage
    When  Navi to StockPage
    And   "StockPage CreatorStock" shared with none
    And   "StockPage CreatorStock" open dropdown "Mit Benutzer teilen"
    And   Select dropdown option "Mohammed Lee"
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
    And   Navi to ProfilePage
    When  Swipe to remove User "Mohammed Lee"
    And   Tap on Confirm
    And   Navi to StockPage
    Then  "StockPage CreatorStock" shared with "Mohammed Lee"
