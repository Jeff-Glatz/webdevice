Feature: Contact Us page

  Scenario: Contact Us page can be viewed with Firefox
    Given a "LocalFirefox" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Chrome
    Given a "LocalChrome" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Chrome (59) on Windows 10
    Given a "Chrome59Windows10" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Firefox (latest) on Mojave
    Given a "FirefoxLatestMojave" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Safari (latest) on Mojave
    Given a "SafariLatestMojave" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with Chrome (latest) on Mojave
    Given a "ChromeLatestMojave" browser
    When I navigate home
    And I navigate to "/contact"

  Scenario: Contact Us page can be viewed with iPhone 8
    Given a "iPhone8" browser
    When I navigate home
    And I navigate to "/contact"