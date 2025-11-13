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
select * from QuizReadings;
*/

/*TC 1: Insert a bunch of users: All set!*/
-- Method: InsertNewUser
CALL InsertNewUser("burnetta@merrimack.edu", FALSE, "Computer Science", NULL, "Ashley", "Burnett"); -- student, 1
CALL InsertNewUser("gilmorel@merrimack.edu", FALSE, "Computer Science", NULL, "Lorelai", "Gilmore"); -- student 2
CALL InsertNewUser("potterh@merrimack.edu", FALSE, "Computer Science", NULL, "Harry", "Potter"); -- student 3
CALL InsertNewUser("grangerh@merrimack.edu", TRUE, "Computer Science", NULL, "Hermione", "Granger"); -- professor 4
CALL InsertNewUser("phillipsa@merrimack.edu", TRUE, "Computer Science", NULL, "Abigail", "Phillips"); -- professor 5
CALL InsertNewUser("mulones@merrimack.edu", FALSE, "Computer Science", NULL, "Sam", "Mulone"); -- student 6
CALL InsertNewUser("wilsond@merrimack.edu", FALSE, "Computer Science", NULL, "David", "Wilson"); -- student 7
CALL InsertNewUser("washingtong@merrimack.edu", TRUE, "Computer Science", NULL, "George", "Washington"); -- professor 8
CALL InsertNewUser("lincolna@merrimack.edu", FALSE, "Computer Science", NULL, "Abraham", "Lincoln"); -- student 9
CALL InsertNewUser("kennedyj@merrimack.edu", FALSE, "Computer Science", NULL, "John", "Kennedy"); -- student 10
CALL InsertNewUser("olonaa@merrimack.edu", FALSE, "Computer Science", NULL, "Alyssa", "Olona"); -- student 11
CALL InsertNewUser("danesl@merrimack.edu", FALSE, "Computer Science", NULL, "Lucas", "Danes"); -- student 12
CALL InsertNewUser("weasleyw@merrimack.edu", FALSE, "Computer Science", NULL, "Ron", "Weasley"); -- student 13
CALL InsertNewUser("sample", FALSE, "sample", NULL, "sample", "sample"); -- student 13

/*TC 1.1: Insert a bunch of classes: All set!*/
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
 -- Method: getEnrolleesByClass (All set!)
call getEnrolleesByClass(105); -- should be lorelai and sam
call getEnrolleesByClass(103); -- alyssa and luke
call getEnrolleesByClass(101); -- sam and david

 -- Method: getStudentsByClass (All set!)
 call getStudentsClasses(3); -- soc and geometry
 call getStudentsClasses(6); -- 101,105
 
 -- Method: getInstructorClasses (All set!)
call getInstructorClasses(8); -- GW: 104, 106, 109
call getInstructorClasses(5); -- AP: 101, 105
call getInstructorClasses(4); -- HG: 102, 103, 107, 108

-- Method: InsertNewReading (All set!)
call InsertNewReading(4, 107, "Prussian History", "Documents/prussia.pdf");
call InsertNewReading(5, 101, "Cells", "Documents/cells.pdf");
call InsertNewReading(8, 104, "Inequality", "Documents/inequality.pdf");
call InsertNewReading(4, 107, "Prussian History", "Documents/prussia.pdf");
call InsertNewReading(5, 101, "Intro to Molecules", "Documents/introtomolecules.pdf");
call InsertNewReading(5, 105, "Objects In Java", "Documents/Objectsinjava.pdf");
call InsertNewReading(8, 106, "Derivatives", "Documents/derivatives.pdf");

-- Method: getReadingByClass (All set!)
call getReadingsByClass(107);
call getReadingsByClass(101);
call getReadingsByClass(104);

-- Methods insertReadingObjectives (All set!)
-- Reminder: dummy info for now, later will be ai genered
call InsertNewReadingObjective(1, 107, "Emperors"); -- Prussian History
call InsertNewReadingObjective(1, 107, "Military"); -- Prussian History
call InsertNewReadingObjective(1, 107, "Politics"); -- Prussian History
call InsertNewReadingObjective(1, 107, "Geography"); -- Prussian History

call InsertNewReadingObjective(6, 105, "Syntax"); -- Java 
call InsertNewReadingObjective(6, 105, "Programming"); -- Java
call InsertNewReadingObjective(6, 105, "Java Objects"); -- Java
call InsertNewReadingObjective(6, 105, "Error Handling"); -- Java
call InsertNewReadingObjective(6, 105, "Classes"); -- Java

 -- Method: InsertNewQuiz (All set!)
call InsertNewQuiz("Fall Of Prussia", 4, 107, @newQuizId);
call InsertNewQuiz("Java Objects", 5, 105, @newQuizId);

-- Method: InsertQuizReading
call InsertQuizReading(1, 1); -- Quiz 1, Prussia reading
call InsertQuizReading(2, 6); -- Quiz 2, Java Reading

-- Method GetQuizObjectives
call getQuizObjectives(2); -- Java
call getQuizObjectives(1); -- Prussia

-- Method: InsertNewQuestion (ERROR)
-- Prussia Quiz
call InsertNewQuestion(1, "Question 1", "easy", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 1);
call InsertNewQuestion(2, "Question 2", "medium", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 1);
call InsertNewQuestion(3, "Question 3", "hard", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 1);
call InsertNewQuestion(4, "Question 4", "easy", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 1);
call InsertNewQuestion(5, "Question 5", "medium", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 1);

-- Java Quiz
call InsertNewQuestion(1, "Question 1", "hard", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 2);
call InsertNewQuestion(2, "Question 2", "easy", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 2);
call InsertNewQuestion(3, "Question 3", "medium", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 2);
call InsertNewQuestion(4, "Question 4", "hard", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 2);
call InsertNewQuestion(5, "Question 5", "easy", "Choice: A", "Choice B", "Choice C", "Choice D", "A",  1, 2);

-- Method: getQuizQuestions
call GetQuizQuestions(1);
call GetQuizQuestions(2);

-- Method: getQuizzesByClass
call getQuizzesByClass(105);

-- Method: SelectStudentObjective
call SelectStudentObjective(11, 1, "Military");
call SelectStudentObjective(11, 1, "Geography");

call getStudentObjectives(11);
select * from StudentObjectives;



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
select qu.questionText, qc.choiceLabel, qc.choiceText
from Quizzes q
join Questions qu on q.quizId = qu.quizId
join QuestionChoices qc on qu.questionId = qc.questionId
where q.quizId = 2;

#display options per question -> double check this when you make it into a proc
select qc.choiceLabel, qc.choiceText
from QuestionChoices qc
join Questions qu on qc.questionId = qu.questionId
where qu.questionId = 1;
 
 SELECT 
        q.quizName
    FROM Quizzes q
    WHERE q.classId = 107;
    
    SHOW DATABASES;

    
 call assignBadge(14);
 
 call getStudentBadges(14);
