Feature: About Us Page

  Scenario: Form can be submitted with Firefox
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/about"

  Scenario: Form can be submitted with Chrome
    Given a "chrome" browser
    When I navigate home
    And I navigate to "/about"

  Scenario: Form can be submitted with Firefox again
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/about"