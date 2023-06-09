Feature: Create student

 #in this scenario the information below is base on documentation so this is reason why we have several tables info
  #
  Scenario: Create student a teacher and verify status code 201
    Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    When I send POST request to "/api/students/student" endpoint with following information
      | first-name      | harold              |
      | last-name       | finch               |
      | email           | harold10@gmail.com  |
      | password        | abc123              |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                   |
      | team-name       | Nukes               |
    Then status code should be 201
    And I delete previously added student


  Scenario: test config
    Given I get env properties

  @wip
  Scenario: Create student a teacher and verify status code 201
    Given I logged Bookit api as "teacher"
    When I send POST request to "/api/students/student" endpoint with following information
      | first-name      | harold              |
      | last-name       | finch               |
      | email           | harold11@gmail.com  |
      | password        | abc123              |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 7                   |
      | team-name       | BugBusters          |
    Then status code should be 201
    And I delete previously added student

