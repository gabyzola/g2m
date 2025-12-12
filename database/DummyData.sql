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
select * from Questions;
select * from QuestionChoices;
select * from QuestionObjectives;
select * from Attempts;
select * from Badges;
select * from Readings;
select * from readingObjectives;
select * from QuizReadings;
select * from AttemptSessions;
select * from AttemptAnswers;
select * from StudentObjectives;

SELECT studentId, totalPoints, badge 
FROM Students 
WHERE studentId = 15;
*/

/*TC 1: Insert a bunch of users: All set!*/
-- Method: InsertNewUser
CALL InsertNewUser("12345","burnetta@merrimack.edu", FALSE, "Computer Science", NULL, "Ashley", "Burnett"); -- student, 1
CALL InsertNewUser("23456", "gilmorel@merrimack.edu", FALSE, "Architecture", NULL, "Lisa", "Gilmore"); -- student 2
CALL InsertNewUser("34567", "smithh@merrimack.edu", FALSE, "Computer Science", NULL, "Harry", "Smith"); -- student 3
CALL InsertNewUser("4567", "doej@merrimack.edu", TRUE, "Computer Science", NULL, "John", "Doe"); -- professor 4
CALL InsertNewUser("56789","doeja@merrimack.edu", TRUE, "Computer Science", NULL, "Jane", "Doe"); -- professor 5
CALL InsertNewUser("67899", "mulones@merrimack.edu", FALSE, "Computer Science", NULL, "Sam", "Mulone"); -- student 6
CALL InsertNewUser("98765", "wilsond@merrimack.edu", FALSE, "Computer Science", NULL, "David", "Wilson"); -- student 7
CALL InsertNewUser("91234", "brownt@merrimack.edu", TRUE, "Computer Science", NULL, "Tina", "Brown"); -- professor 8
CALL InsertNewUser("54321", "carollm@merrimack.edu", FALSE, "Computer Science", NULL, "Meghan", "Carroll"); -- student 9
CALL InsertNewUser("6543", "greyh@merrimack.edu", FALSE, "Computer Science", NULL, "Hannah", "Grey"); -- student 10
CALL InsertNewUser("8765", "olonaa@merrimack.edu", FALSE, "Computer Science", NULL, "Alyssa", "Olona"); -- student 11
CALL InsertNewUser("09090", "jonesl@merrimack.edu", FALSE, "Computer Science", NULL, "Lucas", "Jones"); -- student 12
CALL InsertNewUser("121212", "phillipsa@merrimack.edu", FALSE, "Computer Science", NULL, "Abigail", "Phillips"); -- student 13
CALL InsertNewUser("23232323", "sample", FALSE, "sample", NULL, "sample", "sample"); -- student 13

/*TC 1.1: Insert a bunch of classes: All set!*/
-- Method: InsertNewClass
CALL InsertNewClass(101, "Chemistry", "brownt@merrimack.edu"); -- Tina Brown
CALL InsertNewClass(102, "Biology", "doeja@merrimack.edu"); -- jane doe
CALL InsertNewClass(103, "Psychology", "doeja@merrimack.edu"); -- jane doe
CALL InsertNewClass(104, "Sociology", "doej@merrimack.edu"); -- john doe
CALL InsertNewClass(105, "Object Oriented Programming", "brownt@merrimack.edu");  -- Tina Brown
CALL InsertNewClass(106, "Calculus", "doej@merrimack.edu"); -- john doe
CALL InsertNewClass(107, "European History", "doeja@merrimack.edu"); -- jane doe
CALL InsertNewClass(108, "Asian History", "doeja@merrimack.edu"); -- jane doe
CALL InsertNewClass(109, "Geometry", "doej@merrimack.edu"); -- john doe

/*TC 1.3: Insert ClassEnrollees*/
-- Method: EnrollStudent
CALL EnrollStudent(105, "gilmorel@merrimack.edu");   -- Lisa Gilmore → OOP
CALL EnrollStudent(105, "mulones@merrimack.edu");    -- Sam Mulone → OOP
CALL EnrollStudent(106, "wilsond@merrimack.edu");    -- David Wilson → Calculus
CALL EnrollStudent(106, "carollm@merrimack.edu");   -- Meghan Carroll → Calculus
CALL EnrollStudent(107, "burnetta@merrimack.edu");   -- Ashley Burnett → European History
CALL EnrollStudent(107, "olonaa@merrimack.edu");     -- Alyssa Olona → European History
CALL EnrollStudent(108, "jonesl@merrimack.edu");     -- Lucas Jones → Asian History
CALL EnrollStudent(108, "greyh@merrimack.edu");   -- hannah grey → Asian History
CALL EnrollStudent(109, "smithh@merrimack.edu");    -- Harry Smith → Geometry

CALL EnrollStudent(101, "wilsond@merrimack.edu");     -- David Wilson -> Chemistry
CALL EnrollStudent(101, "mulones@merrimack.edu");     -- Sam Mulone -> Chemistry
CALL EnrollStudent(102, "gilmorel@merrimack.edu");    -- Lisa Gilmore → Biology
CALL EnrollStudent(102, "burnetta@merrimack.edu");   -- Ashley Burnett → Biology
CALL EnrollStudent(102, "carollm@merrimack.edu");   -- meghan carroll -> Biology
CALL EnrollStudent(103, "olonaa@merrimack.edu");     -- Alyssa Olona → Psychology
CALL EnrollStudent(103, "jonesl@merrimack.edu");     -- Lucas Jones → Psychology
CALL EnrollStudent(104, "greyh@merrimack.edu");   -- hannah grey → Sociology
CALL EnrollStudent(104, "smithh@merrimack.edu");    -- Harry Smith -> Sociology

-- after i sign in 
CALL EnrollStudent(104, "pomfretm@merrimack.edu");    -- Mikaela Pomfret -> Sociology
CALL EnrollStudent(105, "pomfretm@merrimack.edu");    -- Mikaela Pomfret -> Intro to Java
CALL EnrollStudent(107, "pomfretm@merrimack.edu");    -- Mikaela Pomfret -> France
CALL EnrollStudent(110, "pomfretm@merrimack.edu");    -- Mikaela Pomfret -> Sociology
CALL EnrollStudent(111, "pomfretm@merrimack.edu");    -- Mikaela Pomfret -> Intro to Java

 /*TC 4.1: Professor gets all class enrollee*/
 -- Method: getEnrolleesByClass (All set!)
call getEnrolleesByClass(105); -- should be lisa and sam
call getEnrolleesByClass(103); -- alyssa and lucas
call getEnrolleesByClass(101); -- sam and david

 -- Method: getStudentsByClass (All set!)
 call getStudentsClasses(3); -- soc and geometry
 call getStudentsClasses(6); -- 101,105
 
 -- Method: getInstructorClasses (All set!)
call getInstructorClasses(8); -- GW: 104, 106, 109
call getInstructorClasses(5); -- AP: 101, 105
call getInstructorClasses(4); -- HG: 102, 103, 107, 108

-- Method: InsertNewReading (All set!)
call InsertNewReading(4, 107, "French History"); -- 1
call InsertNewReading(5, 101, "Cells"); -- 2
call InsertNewReading(8, 104, "Inequality"); -- 3
call InsertNewReading(4, 107, "Renaissance"); -- 4
call InsertNewReading(5, 101, "Intro to Molecules"); -- 5
call InsertNewReading(5, 105, "Objects In Java"); -- 6
call InsertNewReading(8, 106, "Derivatives"); -- 7

-- Method: getReadingByClass (All set!)
call getReadingsByClass(107);
call getReadingsByClass(101);
call getReadingsByClass(106);

-- Methods insertReadingObjectives (All set!)
call InsertNewReadingObjective(1, 107, "Emperors"); -- French History
call InsertNewReadingObjective(1, 107, "Military"); -- French History
call InsertNewReadingObjective(1, 107, "Politics"); -- French History
call InsertNewReadingObjective(1, 107, "Geography"); -- French History

call InsertNewReadingObjective(2, 101, "Mitochondria"); -- cells
call InsertNewReadingObjective(2, 101, "Cytoplasm"); -- cells

call InsertNewReadingObjective(5, 101, "Compounds"); -- chem
call InsertNewReadingObjective(5, 101, "Molecules"); -- chem
call InsertNewReadingObjective(5, 101, "Elements"); -- chem
call InsertNewReadingObjective(5, 101, "Reactions"); -- chem

call InsertNewReadingObjective(6, 105, "Syntax"); -- Java 
call InsertNewReadingObjective(6, 105, "Programming"); -- Java
call InsertNewReadingObjective(6, 105, "Java Objects"); -- Java
call InsertNewReadingObjective(6, 105, "Error Handling"); -- Java
call InsertNewReadingObjective(6, 105, "Classes"); -- Java

call InsertNewReadingObjective(7, 106, "Velocity"); -- calc
call InsertNewReadingObjective(7, 106, "Notation"); -- calc
call InsertNewReadingObjective(7, 106, "Graphs"); -- calc
call InsertNewReadingObjective(7, 106, "Newton"); -- calc
call InsertNewReadingObjective(7, 106, "Integrals"); -- calc

 -- Method: InsertNewQuiz (All set!)
call InsertNewQuiz("French Revolution", 4, 107, @newQuizId);
call InsertNewQuiz("Java Objects", 5, 105, @newQuizId);
call InsertNewQuiz("Introduction to Java", 5, 105, @newQuizId);

-- Method: InsertQuizReading
call InsertQuizReading(1, 1); -- Quiz 1, French reading
call InsertQuizReading(2, 6); -- Quiz 2, Java Reading
call InsertQuizReading(3, 8);

-- Method GetQuizObjectives
call getQuizObjectives(2); -- Java
call getQuizObjectives(1); -- Prussia
call getQuizObjectives(11);

call getQuizObjectives(3);

-- Method: InsertNewQuestion (ERROR)
-- French Revolution Quiz
-- French Revolution Quiz
call InsertNewQuestion(1, "Who was the king of France at the start of the French Revolution?", "easy", 
    "Louis XVI", "Napoleon Bonaparte", "Robespierre", "Louis XIV", "A", 1, 1);
call InsertNewQuestion(2, "What event is commonly considered the start of the French Revolution?", "medium",
    "The Storming of the Bastille", "The Reign of Terror", "The Tennis Court Oath", "Execution of Louis XVI", "A", 1, 1);
call InsertNewQuestion(3, "What was the radical political group that gained power during the Revolution?", "hard",
    "The Jacobins", "The Girondins", "The Royalists", "The Sans-Culottes", "A", 1, 1);
call InsertNewQuestion(4, "What document outlined the basic rights of French citizens during the Revolution?", "easy",
    "The Declaration of the Rights of Man and of the Citizen", "The Magna Carta", "The Napoleonic Code", "The Social Contract", "A", 1, 1);
call InsertNewQuestion(5, "Which device became infamous as a tool of execution during the Revolution?", "medium",
    "The Guillotine", "The Rack", "The Iron Maiden", "The Firing Squad", "A", 1, 1);

-- Intro to Java Quiz
call InsertNewQuestion(1, "Which keyword is used to define a class in Java?", "hard",
    "class", "struct", "define", "object", "A", 1, 2);
call InsertNewQuestion(2, "What does JVM stand for?", "easy",
    "Java Virtual Machine", "Java Variable Manager", "Joint Virtual Module", "Java Version Monitor", "A", 1, 2);
call InsertNewQuestion(3, "Which data type is used to store true/false values?", "medium",
    "boolean", "int", "char", "float", "A", 1, 2);
call InsertNewQuestion(4, "What symbol is used for single-line comments in Java?", "hard",
    "//", "/*", "#", "<!-- -->", "A", 1, 2);
call InsertNewQuestion(5, "Which of the following is a valid Java variable name?", "easy",
    "totalScore", "2score", "total-score", "total score", "A", 1, 2);

-- Method: getQuizQuestions
call GetQuizQuestions(1);
call GetQuizQuestions(2);
call GetQuizQuestions(6);
call getReadingObjectives(1);

-- Method: getQuizzesByClass
call getQuizzesByClass(105);

call getStudentObjectives(11);
call getStudentObjectives(2);
call getStudentObjectives(7);

call assignBadge(14);
call getStudentBadges(14);

#test: pass
call canCreateQuiz(7, 101);
call deleteClassEnrollee(104, 13);
call DeleteReading(4);
call DeleteQuiz(4);

call DeleteStudentObjectives(1);
call DeleteStudentObjectives(2);
call DeleteStudentObjectives(15);

call LookupUserBySub("109648142961992634293");
call LookupUserBySub("8765");
call isInstructorCheck(1);

call GetSessionResults(4);
call GetSessionResults2(15);

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
where q.quizId = 8;

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
