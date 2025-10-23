/*DUMMY DATA*/
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
select * from Badges;
select * from Readings;
select * from readingObjectives;
*/

/*TC 1: Insert a bunch of users*/
-- Method: InsertNewUser
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
-- Method: InsertNewClass
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
-- Method: EnrollStudent
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
 -- Method: getEnrolleesByClass
 call getEnrolleesByClass(105); -- should be lorelai and sam
 
 -- Method: getStudentsByClass
 call getStudentsClasses(3); -- soc and geometry
 
 -- Method: getInstructorClasses
call getInstructorClasses(8);
 
 -- Method: InsertNewQuiz
call InsertNewQuiz("Fall Of Prussia", 4, 107);

-- ADD READINGS (INSERTS FOR NOW)
INSERT INTO Readings (instructorId, classId, readingName, filePath, uploadDate)
VALUES
(4, 107, "Prussian History", "Documents/prussia.pdf", null),
(5, 101, "Intro to Molecules", "Documents/introtomolecules.pdf", null),
(5, 105, "Objects In Java", "Documents/Objectsinjava.pdf", null),
(8, 106, "Derivatives", "Documents/derivatives.pdf", null);

select * from Readings;

-- ADD LEARNING OBJECTIVES FROM READING
INSERT INTO readingObjectives (readingId, classId, objectiveName, isApproved)
VALUES
(1, 107, "Emperors", null),
(1, 107, "Politics", null),
(1, 107, "Geography", null),
(1, 107, "Military", null),
(2, 101, "Scientific Method", null),
(2, 101, "Molecules", null),
(2, 101, "Elements", null),
(2, 101, "Interactions", null),
(3, 105, "Objects", null),
(3, 105, "Methods", null),
(3, 105, "Testing", null),
(3, 105, "Java Language", null),
(3, 105, "Algorithms", null),
(4, 106, "Integrals", null),
(4, 106, "Graphing", null);

 select * from readingObjectives;
 
-- Method: InsertNewQuestion
-- Question number: 1, question text: Question 1, difficulty: medium, choices: 1-4, correct choice: A, LO: 4 military, Quiz: 1
call InsertNewQuestion(1, "Why did Prussia fall?", "Medium", "Military", "Government", "Revolution", "All of the above", "A", 4, 1);
call InsertNewQuestion(2, "Why was military bad?", "Easy", "No training", "Outdated tactics", "They were surrounded", "Revolution", "C", 4, 1);
call InsertNewQuestion(3, "Question 3", "hard", "A1", "A2", "A3", "A4", "A", 4, 1);

call InsertNewQuestion(4, "Question 4", "Medium", "A1", "A2", "A3", "A4", "A", 2, 1);
call InsertNewQuestion(5, "Question 5", "Easy", "A1", "A2", "A3", "A4", "A", 2, 1);
call InsertNewQuestion(6, "Question 6", "Medium", "A1", "A2", "A3", "A4", "A", 2, 1);

call InsertNewQuestion(7, "Question 7", "hard", "A1", "A2", "A3", "A4", "A", 3, 1);

select * from Questions, QuestionObjectives, QuestionChoices; -- everything looks correct
 
 -- GOOD UP UNTIL HERE
 
 -- Method: DeleteQuestion
call DeleteQuestion(3); -- deletes question number 4!
 
-- Method: DeleteClass
  
-- Method: DeleteClassEnrollee
   
-- Method: DeleteChoice
    
-- Method: DeleteUser

-- Method: DeleteQuiz

-- Method: SelectStudentObjective

-- Method: GetNextQuestion

-- Method: SubmitAnswer

-- Method: GetQuizScore

#display quiz info as a whole
select q.quizName, qu.questionText, qc.choiceLabel, qc.choiceText
from Quizzes q
join Questions qu on q.quizId = qu.quizId
join QuestionChoices qc on qu.questionId = qc.questionId
where q.quizId = 1;

#display quiz questions
select q.quizName, qu.questionText, qc.choiceLabel, qc.choiceText
from Quizzes q
join Questions qu on q.quizId = qu.quizId
join QuestionChoices qc on qu.questionId = qc.questionId
where q.quizId = 1;

#display options per question -> double check this when you make it into a proc
select qc.choiceLabel, qc.choiceText
from QuestionChoices qc
join Questions qu on qc.questionId = qu.questionId
where qu.questionId = 1;
 
 SELECT 
        q.quizName
    FROM Quizzes q
    WHERE q.classId = 107;
 