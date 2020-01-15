Feature: Device definition

  Scenario: Eagerly acquires the default device and allows changing device in test
    When I navigate home
    And I navigate to "/about"
    When a "Chrome" browser
    And I navigate home
    When a "Firefox" browser
    And I navigate home
