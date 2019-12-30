Feature: Contact Us Page

  Scenario: Page can be viewed with Firefox
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Page can be viewed with Chrome
    Given a "chrome" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Page can be viewed with Firefox again
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Page can be viewed with Custom
    Given a "custom" browser
    When I navigate home
    And I navigate to "/contact"