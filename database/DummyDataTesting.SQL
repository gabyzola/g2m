USE QuizzingDB;   

INSERT INTO Badges (badgeName, description, pointThreshold)
VALUES
('Rookie', 'Earned by completing your first quiz.', 1),
('Steady Learner', 'Reach 100 total points.', 100),
('Knowledge Collector', 'Reach 500 total points.', 500),
('Quiz Master', 'Reach 700 total points.', 700),
('Legend', 'Reach 1000 total points.', 1000);

/*ALL SELECT STATEMENTS
select * from UserAccounts;
select * from Students;
select * from Instructors;
select * from Classroom;
select * from ClassEnrollees;
select * from Quizzes;
select * from LearningObjectives;
select * from Questions;
select * from QuestionChoices;
select * from QuestionObjectives;
select * from Attempts;
*/

/*TC 1: Insert a bunch of users*/
CALL InsertNewUser("ashley_burnett", "burnetta@merrimack.edu", FALSE, "Computer Science", NULL, "Ashley", "Burnett"); -- student, 1
CALL InsertNewUser("lorelai_gilmore", "gilmorel@merrimack.edu", FALSE, "Computer Science", NULL, "Lorelai", "Gilmore"); -- student 2
CALL InsertNewUser("harry_potter", "potterh@merrimack.edu", FALSE, "Computer Science", NULL, "Harry", "Potter"); -- student 3
CALL InsertNewUser("hermione_granger", "grangerh@merrimack.edu", TRUE, "Computer Science", NULL, "Hermione", "Granger"); -- professor 4
CALL InsertNewUser("abigail_phillips", "phillipsa@merrimack.edu", TRUE, "Computer Science", NULL, "Abigail", "Phillips"); -- professor 5
CALL InsertNewUser("sam_mulone", "mulones@merrimack.edu", FALSE, "Computer Science", NULL, "Sam", "Mulone"); -- student 6
CALL InsertNewUser("david_wilson", "wilsond@merrimack.edu", FALSE, "Computer Science", NULL, "David", "Wilson"); -- student 7
CALL InsertNewUser("george_washington", "washingtong@merrimack.edu", TRUE, "Computer Science", NULL, "George", "Washington"); -- professor 8
CALL InsertNewUser("abraham_lincoln", "lincolna@merrimack.edu", FALSE, "Computer Science", NULL, "Abraham", "Lincoln"); -- student 9
CALL InsertNewUser("john_kennedy", "kennedyj@merrimack.edu", FALSE, "Computer Science", NULL, "John", "Kennedy"); -- student 10
CALL InsertNewUser("alyssa_olona", "olonaa@merrimack.edu", FALSE, "Computer Science", NULL, "Alyssa", "Olona"); -- student 11
CALL InsertNewUser("lucas_danes", "danesl@merrimack.edu", FALSE, "Computer Science", NULL, "Lucas", "Danes"); -- student 12
CALL InsertNewUser("ron_weasley", "weasleyw@merrimack.edu", FALSE, "Computer Science", NULL, "Ron", "Weasley"); -- student 13

/*TC 1.1: Insert a bunch of classes*/
CALL InsertNewClass(101, "Chemistry", "phillipsa@merrimack.edu"); -- Abigail Phillips
CALL InsertNewClass(102, "Biology", "grangerh@merrimack.edu"); -- Hermione Granger
CALL InsertNewClass(103, "Psychology", "grangerh@merrimack.edu"); -- Hermione Granger
CALL InsertNewClass(104, "Sociology", "washingtong@merrimack.edu"); -- George Washington
CALL InsertNewClass(105, "Object Oriented Programming", "phillipsa@merrimack.edu");  -- Abigail Phillips
CALL InsertNewClass(106, "Calculus", "washingtong@merrimack.edu"); -- George Washington
CALL InsertNewClass(107, "European History", "grangerh@merrimack.edu"); -- Hermione Granger
CALL InsertNewClass(108, "Asian History", "grangerh@merrimack.edu"); -- Hermione Granger
CALL InsertNewClass(109, "Geometry", "washingtong@merrimack.edu"); -- George Washington

/*TC 1.3: Insert ClassEnrollees*/
/* Enrollments for new students */
CALL EnrollStudent(105, "gilmorel@merrimack.edu");   -- Lorelai Gilmore → OOP
CALL EnrollStudent(105, "mulones@merrimack.edu");    -- Sam Mulone → OOP
CALL EnrollStudent(106, "wilsond@merrimack.edu");    -- David Wilson → Calculus
CALL EnrollStudent(106, "lincolna@merrimack.edu");   -- Abraham Lincoln → Calculus
CALL EnrollStudent(107, "kennedyj@merrimack.edu");   -- John Kennedy → European History
CALL EnrollStudent(107, "olonaa@merrimack.edu");     -- Alyssa Olona → European History
CALL EnrollStudent(108, "danesl@merrimack.edu");     -- Lucas Danes → Asian History
CALL EnrollStudent(108, "weasleyw@merrimack.edu");   -- Ron Weasley → Asian History
CALL EnrollStudent(109, "potterh@merrimack.edu");    -- Harry Potter → Geometry

CALL EnrollStudent(101, "wilsond@merrimack.edu");     -- David Wilson -> Chemistry
CALL EnrollStudent(101, "mulones@merrimack.edu");     -- Sam Mulone -> Chemistry
CALL EnrollStudent(102, "gilmorel@merrimack.edu");    -- Lorelai Gilmore → Biology
CALL EnrollStudent(102, "kennedyj@merrimack.edu");   -- John Kennedy → Biology
CALL EnrollStudent(102, "lincolna@merrimack.edu");   -- Abraham Lincoln -> Biology
CALL EnrollStudent(103, "olonaa@merrimack.edu");     -- Alyssa Olona → Psychology
CALL EnrollStudent(103, "danesl@merrimack.edu");     -- Lucas Danes → Psychology
CALL EnrollStudent(104, "weasleyw@merrimack.edu");   -- Ron Weasley → Sociology
CALL EnrollStudent(104, "potterh@merrimack.edu");    -- Harry Potter -> Sociology
 
 /*TC 4.1: Professor gets all class enrollee*/
 -- SUCCESS
 call getEnrolleesByClass(105); -- should be lorelai and sam
 
 call getStudentsClasses(3); -- soc and geometry

 
 

