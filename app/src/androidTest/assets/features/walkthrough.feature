Feature: App walkthrough

  Background:
    Given Init Mocks
    When  Run App

  Scenario: Empty App walkthrough
    Given Tap on NaviToRegisterButton
    When  Register with first name "Bob", last name "TheDeal", email "newUser@gmx.de" and password "Password321!"
    Then  OverviewPage rendered
    And   No Checklists displayed
    When  Tap on StockButton
    Then  StockPage rendered
    And   No Stocks displayed
    When  On Back
    And   Tap on ProfileButton
    Then  ProfilePage rendered
    And   No Stocks displayed
    And   No connected Users displayed
    When  Tap on SettingsButton
    Then  SettingsPage rendered
    When  On Back
    And   On Back
    And   Tap on NewChecklistButton
    And   Input "NewChecklist" as Checklist name
    And   Tap on CreateChecklistButton
    And   Tap on Checklist "NewChecklist"
    Then  ChecklistPage rendered
    And   No Items displayed
    When  Tap on AddItemButton
    Then  SelectItemsPage rendered
    And   No Items displayed

