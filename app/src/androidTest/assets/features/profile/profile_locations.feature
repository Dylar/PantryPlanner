Feature: Profile locations management

  Scenario: Create a new Location from the ProfilePage
    Given Mock default User
    And   Start on ProfilePage
    When  Tap NewLocationButton on ProfilePage
    Then  NewLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location with name "Home" should be shown on ProfilePage
