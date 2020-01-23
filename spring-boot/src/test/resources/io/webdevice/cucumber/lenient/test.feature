Feature: Device definition

  Scenario: Eagerly acquires the default device and allows changing device in test
    When I navigate home
    And I navigate to "/tasks"
    When a "Chrome" browser is used
    And I navigate home
    When the default browser is used
    And I navigate home
