Feature: Contact Us Form

  Scenario: Form can be submitted with Firefox
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Form can be submitted with Chrome
    Given a "chrome" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Form can be submitted with Firefox again
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/contact"