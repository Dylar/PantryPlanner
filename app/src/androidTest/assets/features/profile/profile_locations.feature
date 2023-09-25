Feature: Profile Locations management

  Background:
    Given Init default Mocks
    And   Start on ProfilePage
    And   Location "Home" should NOT be shown
    And   Location "CreatorLocation" should be shown
    And   Location "SharedLocation" should be shown

  Scenario: Create a new Location
    When  Tap NewLocationButton
    And   AddEditLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location "Home" should be shown

  Scenario: Try to remove a Location, but cancel confirmation
    When  Swipe to remove Location "CreatorLocation"
    And   Tap on dismiss
    Then  Location "CreatorLocation" should be shown

  Scenario: Remove a Location
    When  Swipe to remove Location "CreatorLocation"
    And   Tap on confirm
    Then  Location "CreatorLocation" should NOT be shown

  Scenario: Remove a shared Location
    When  Swipe to remove Location "SharedLocation"
    And   Tap on confirm
    Then  Location "SharedLocation" should NOT be shown

  Scenario: Prevent non-creator from editing a Location
    When  LongPress on Location "SharedLocation"
    And   AddEditLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location "Home" should NOT be shown
    And   Location "SharedLocation" should be shown
    And   SnackBar shown: "Nur der Ersteller kann den Ort ändern"

  Scenario: Edit a created Location
    When  LongPress on Location "CreatorLocation"
    And   AddEditLocationDialog is displayed
    And   Input "Home" as Location name
    And   Tap on CreateLocationButton
    Then  Location "Home" should be shown
    And   Location "CreatorLocation" should NOT be shown
