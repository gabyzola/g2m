USE QuizzingDB;  

-- for testing purposes, no one really has to look at this if they don't want to but i might as well include it

-- =========================
-- 1) UserAccounts
-- =========================
INSERT INTO UserAccounts (userId, googleId, username, email, isInstructor)
VALUES
(1, 'google-001', 'susan_smith', 'smiths@merrimack.edu', TRUE),
(2, 'google-002', 'jane_doe', 'doej@merrimack.edu', TRUE),
(3, 'google-003', 'john_johnson', 'johnsonj@merrimack.edu', TRUE),
(4, 'google-004', 'jack_student', 'studentj@merrimack.edu', FALSE),
(5, 'google-005', 'meghan_person', 'personm@merrimack.edu', FALSE),
(6, 'google-006', 'josh_guy', 'guyj@merrimack.edu', FALSE);

-- =========================
-- 2) Instructors
-- =========================
INSERT INTO Instructors (instructorId, schoolSubject)
VALUES
(1, 'Computer Science'),
(2, 'Mathematics'),
(3, 'History');


-- =========================
-- 3) Students
-- =========================
INSERT INTO Students (studentId, badge, totalPoints, major)
VALUES
(4, NULL, 0, "Mathematics"), -- Jack
(5, NULL, 0, "History"), -- Meghan
(6, NULL, 0, "Computer Science"); -- Josh

-- =========================
-- 4) Classrooms
-- =========================
INSERT INTO Classroom (classId, className, instructorId)
VALUES
(101, 'Intro to Databases', 1), -- Smith
(102, 'Discrete Mathematics', 2), -- Doe
(103, 'World History', 3); -- Johnson

-- =========================
-- 5) ClassEnrollees
-- =========================
INSERT INTO ClassEnrollees (classId, studentId)
VALUES
(101, 6),  -- Josh in Databases
(103, 5),  -- Meghan in history
(102, 4);  -- Jack in Math

-- =========================
-- 6) Badges
-- =========================
INSERT INTO Badges (badgeName, description, pointThreshold)
VALUES
('Rookie', 'Earned by completing your first quiz.', 1),
('Steady Learner', 'Reach 100 total points.', 100),
('Knowledge Collector', 'Reach 500 total points.', 500),
('Quiz Master', 'Reach 700 total points.', 700),
('Legend', 'Reach 1000 total points.', 1000);


/*ALL SELECT STATEMENTS*/
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

/*TC 1: Insert a bunch of users*/
CALL InsertNewUser("google_7", "ashley_burnett", "burnetta@merrimack.edu", FALSE, "Computer Science", NULL); -- student, 7
CALL InsertNewUser("google_8", "lorelai_gilmore", "gilmorel@merrimack.edu", FALSE, "Computer Science", NULL); -- student 8
CALL InsertNewUser("google_9", "harry_potter", "potterh@merrimack.edu", FALSE, "Computer Science", NULL); -- student 9
CALL InsertNewUser("google_10", "hermione_granger", "grangerh@merrimack.edu", TRUE, "Computer Science", NULL); -- professor 10
CALL InsertNewUser("google_11", "abigail_phillips", "phillipsa@merrimack.edu", TRUE, "Computer Science", NULL); -- professor 11
CALL InsertNewUser("google_12", "sam_mulone", "mulones@merrimack.edu", FALSE, "Computer Science", NULL); -- student 12
CALL InsertNewUser("google_13", "david_wilson", "wilsond@merrimack.edu", FALSE, "Computer Science", NULL); -- student 13
CALL InsertNewUser("google_14", "george_washington", "washingtong@merrimack.edu", TRUE, "Computer Science", NULL); -- professor 14
CALL InsertNewUser("google_15", "abraham_lincoln", "lincolna@merrimack.edu", FALSE, "Computer Science", NULL); -- student 15
CALL InsertNewUser("google_16", "john_kennedy", "kennedyj@merrimack.edu", FALSE, "Computer Science", NULL); -- student 16
CALL InsertNewUser("google_17", "alyssa_olona", "olonaa@merrimack.edu", FALSE, "Computer Science", NULL); -- student 17
CALL InsertNewUser("google_18", "lucas_danes", "danesl@merrimack.edu", FALSE, "Computer Science", NULL); -- student 18
CALL InsertNewUser("google_19", "ron_weasley", "weasleyw@merrimack.edu", FALSE, "Computer Science", NULL); -- student 19

/*TC 1.1: Insert a bunch of classes*/
CALL InsertNewClass(105, "Object Oriented Programming", 11); 
CALL InsertNewClass(106, "Calculus", 14); 
CALL InsertNewClass(107, "European History", 10); 
CALL InsertNewClass(108, "Asian History", 10);
CALL InsertNewClass(109, "Geometry", 14); 

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


/*TC 2: Insert New Class*/
CALL InsertNewClass(104, "Algebra", 2); -- should be four classes, Dr. Doe should have discrete math and algebra

/*TC 2.1: Enroll Jack*/
CALL EnrollStudent(104, "studentj@merrimack.edu"); -- should be discrete math and algebra

/*TC 3: Create new student + enroll 2 students under one class*/
-- already created ashley
CALL EnrollStudent(101, "burnetta@merrimack.edu"); -- should be in database classenrollees with Josh

/*TC: 4: Professor Deletes Classroom*/
 Call deleteClass(101); -- databases should not be listed and should not have any classenrollees
 
 /*TC 4.1: Professor removes class enrollee*/