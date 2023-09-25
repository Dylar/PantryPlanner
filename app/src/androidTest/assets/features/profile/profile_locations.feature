Feature: Profile locations management

  Scenario: Create a new Location from the ProfilePage
    Given Init default Mocks
    And   Start on ProfilePage
    When  Tap NewLocationButton on ProfilePage
    Then  NewLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location with name "Home" should be shown on ProfilePage

  Scenario: Remove a Location from the ProfilePage
    Given Init default Mocks
    And   Start on ProfilePage
    When  Swipe to remove Location "DefaultLocation" on ProfilePage
    And   Tap on confirm
    Then  Location with name "DefaultLocation" should NOT be shown on ProfilePage
