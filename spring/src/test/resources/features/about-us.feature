Feature: About Us page

  @direct
  Scenario: About Us page can be viewed with Firefox
    Given a "LocalFirefox" browser is used
    When I navigate home
    And I navigate to "/about"

  @direct
  Scenario: About Us page can be viewed with Chrome
    Given a "LocalChrome" browser is used
    When I navigate home
    And I navigate to "/about"

  @sauce
  Scenario: About Us page can be viewed with Chrome (59) on Windows 10
    Given a "Chrome59Windows10" browser is used
    When I navigate home
    And I navigate to "/about"

  @sauce
  Scenario: About Us page can be viewed with Firefox (latest) on Mojave
    Given a "FirefoxLatestMojave" browser is used
    When I navigate home
    And I navigate to "/about"

  @sauce
  Scenario: About Us page can be viewed with Safari (latest) on Mojave
    Given a "SafariLatestMojave" browser is used
    When I navigate home
    And I navigate to "/about"

  @sauce
  Scenario: About Us page can be viewed with Chrome (latest) on Mojave
    Given a "ChromeLatestMojave" browser is used
    When I navigate home
    And I navigate to "/about"

  @sauce
  Scenario: About Us page can be viewed with iPhone 8
    Given a "iPhone 8" browser is used
    When I navigate home
    And I navigate to "/about"