Feature: Contact Us page

  Scenario: Contact Us page can be viewed with Firefox
    Given a "firefox" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Chrome
    Given a "chrome" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with FirefoxMacOSv33
    Given a "FirefoxMacOSv33" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with iPhone XS
    Given a "iPhoneXS" browser
    When I navigate home
    And I navigate to "/contact"