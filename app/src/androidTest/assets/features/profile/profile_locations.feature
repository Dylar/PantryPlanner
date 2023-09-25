Feature: Profile Locations management

  Scenario: Create a new Location from the ProfilePage
    Given Init default Mocks
    And   Start on ProfilePage
    When  Tap NewLocationButton on ProfilePage
    Then  AddEditLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location with name "Home" should be shown on ProfilePage

  Scenario: Remove a Location from the ProfilePage
    Given Init default Mocks
    And   Start on ProfilePage
    When  Swipe to remove Location "DefaultLocation" on ProfilePage
    And   Tap on confirm
    Then  Location with name "DefaultLocation" should NOT be shown on ProfilePage

  Scenario: Try to remove a Location from the ProfilePage, but cancel confirmation
    Given Init default Mocks
    And   Start on ProfilePage
    When  Swipe to remove Location "DefaultLocation" on ProfilePage
    And   Tap on dismiss
    Then  Location with name "DefaultLocation" should be shown on ProfilePage

  Scenario: Edit Location
    Given Init default Mocks
    And   Start on ProfilePage
    And   Location with name "NEW_NAME" should NOT be shown on ProfilePage
    When  LongPress on Location "DefaultLocation" on ProfilePage
    Then  AddEditLocationDialog is displayed
    And   Input "NEW_NAME" as Location name
    And   Tap on CreateLocationButton
    And   Location with name "NEW_NAME" should be shown on ProfilePage
