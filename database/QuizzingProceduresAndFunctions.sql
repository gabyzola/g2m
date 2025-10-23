USE QuizzingDB;

/*Functions*/

#make sure question isn't a repeat when inserting 
DELIMITER $$
DROP FUNCTION IF EXISTS doesQuestionExist;$$
CREATE FUNCTION doesQuestionExist(myQuestionText TEXT)
RETURNS BOOLEAN DETERMINISTIC
BEGIN
declare result int;
SELECT COUNT(*) INTO result FROM Questions WHERE questionText = myQuestionText;
    RETURN (result > 0);
    END;$$
DELIMITER ;

#check if a user already exists by email
DELIMITER $$
DROP FUNCTION IF EXISTS doesUserExist $$
CREATE FUNCTION doesUserExist(myEmail VARCHAR(150))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
declare result int;
SELECT COUNT(*) INTO result FROM UserAccounts WHERE email = myEmail;
    RETURN (result > 0);
END $$
DELIMITER ;

#check if student answer is correct

/*TABLE INSERTIONS*/

#insert instructor to instructor table (called automatically in dal when account is created based on flag)
#insert a student  to student table, automatically called when user is flagged as student
#the above is gonna be done in one proc
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewUser $$
CREATE PROCEDURE InsertNewUser (
    IN myUsername VARCHAR(100),
    IN myEmail VARCHAR(150),
    IN myIsInstructor BOOLEAN,
    IN myMajor VARCHAR(100),        
    IN mySchoolSubject VARCHAR(50),
    IN myFirstName varchar(20),
    IN myLastName varchar(50)
)
BEGIN
    DECLARE newUserId INT;

    INSERT INTO UserAccounts (username, email, isInstructor, firstName, lastName)
    VALUES (myUsername, myEmail, myIsInstructor, myFirstName, myLastName);
    
    -- Capture the auto-incremented ID from the last insert
    SET newUserId = LAST_INSERT_ID();

    IF myIsInstructor = TRUE THEN
        INSERT INTO Instructors (instructorId, schoolSubject, firstName, lastName, email)
        VALUES (newUserId, mySchoolSubject, myFirstName, myLastName, myEmail);
    ELSE
        INSERT INTO Students (studentId, firstName, lastName, badge, totalPoints, major, email)
        VALUES (newUserId, myFirstName, myLastName, NULL, 0, myMajor, myEmail);
    END IF;
END $$

DELIMITER ;

#class creation procedure
DELIMITER $$

DROP PROCEDURE IF EXISTS InsertNewClass $$
CREATE PROCEDURE InsertNewClass (
	IN myClassId int,
    IN myClassName VARCHAR(100),
    IN myEmail VARCHAR(150)
)
BEGIN
    DECLARE instructorIdFound INT;
    DECLARE instructorFirstName VARCHAR(20);
    DECLARE instructorLastName VARCHAR(50);

    -- Look up instructor details using the email
    SELECT instructorId, firstName, lastName
    INTO instructorIdFound, instructorFirstName, instructorLastName
    FROM Instructors
    WHERE email = myEmail
    LIMIT 1;

    -- Handle the case where no instructor is found
    IF instructorIdFound IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Instructor with the provided email was not found.';
    ELSE
        -- Insert the new class using retrieved instructor info
        INSERT INTO Classroom (classId, className, firstName, lastName, instructorId)
        VALUES (myClassId, myClassName, instructorFirstName, instructorLastName, instructorIdFound);
    END IF;
END $$

DELIMITER ;


#enroll student in class
DELIMITER $$
DROP PROCEDURE IF EXISTS EnrollStudent $$
CREATE PROCEDURE EnrollStudent (
    IN myClassId INT,
    IN myEmail VARCHAR(150)
)
BEGIN
    DECLARE vUserId INT;
    DECLARE vStudentId INT;

    -- Look up the userId by email
    SELECT userId INTO vUserId
    FROM UserAccounts
    WHERE email = myEmail;

    -- If no user found, throw an error
    IF vUserId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No user found with this email.';
    END IF;

    -- Ensure the user is a student
    SELECT studentId INTO vStudentId
    FROM Students
    WHERE studentId = vUserId;

    IF vStudentId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User exists but is not a student.';
    END IF;

    -- Check if already enrolled
    IF EXISTS (
        SELECT 1 FROM ClassEnrollees
        WHERE classId = myClassId AND studentId = vStudentId
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Student is already enrolled in this class.';
    ELSE
        -- Insert enrollment
        INSERT INTO ClassEnrollees (classId, studentId)
        VALUES (myClassId, vStudentId);
    END IF;
END $$

DELIMITER ;

#quiz creation procedure
#add in some functions for error checking
delimiter $$
drop procedure if exists InsertNewQuiz;
create procedure InsertNewQuiz (
myQuizName varchar(100),
myInstructorId int,
myClassId int) 
begin

DECLARE newQuizId INT;
 
insert into Quizzes (quizId, quizName, instructorId, classId)
    values (newQuizId, myQuizName, myInstructorId, myClassId);
    
SET newQuizId = LAST_INSERT_ID();
end $$
delimiter ;

#insert a question to a quiz with choices and objective
#MAKE SURE QUIZID EXISTS, MAKE SURE LEARNING OBJECTIVE IS RELEVANT 
delimiter $$
DROP PROCEDURE IF EXISTS InsertNewQuestion $$
CREATE PROCEDURE InsertNewQuestion (
myQuestionNumber int,
myQuestionText text,
myDifficulty varchar(10),
myChoiceA varchar(300),
myChoiceB varchar(300),
myChoiceC varchar(300),
myChoiceD varchar(300),
myCorrectAnswer CHAR(1),
myObjectiveId int, 
myQuizId int)
BEGIN

declare newQuestionId int;
declare correctChoiceId int;

if not doesQuestionExist(myQuestionText) then
        insert into Questions (questionNumber, questionText, difficulty, quizId)
        values (myQuestionNumber, myQuestionText, myDifficulty, myQuizId);
        
        set newQuestionId = LAST_INSERT_ID();
        
        -- professor will now add choices
        insert into QuestionChoices (questionId, choiceLabel, choiceText)
        values (newQuestionId, 'A', myChoiceA),
               (newQuestionId, 'B', myChoiceB),
               (newQuestionId, 'C', myChoiceC),
               (newQuestionId, 'D', myChoiceD);
               
               -- Find correct choice_id
    select choiceId into correctChoiceId
    from QuestionChoices
    where questionId = newQuestionId and choiceLabel = myCorrectAnswer;
               
	    -- assign correct choice to the question id
        update Questions
        set correct_choice_id = correctChoiceId
        WHERE questionId = newQuestionId;
        
        -- assign objective to question
        -- THIS MIGHT NEED TO BE OBJECTIVE NAME INSTEAD
        insert into QuestionObjectives (questionId, objectiveId)
        values (newQuestionId, myObjectiveId);
    end if;
end $$
delimiter ;

#insert new reading

#Insert new readingObjective

#insert objective into a question

/*DELETE FROM TABLES*/

#delete question from question table
DELIMITER $$
drop procedure if exists DeleteQuestion $$
create procedure DeleteQuestion (myQuestionId int
)
begin
delete from Questions where questionId = myQuestionId;
end $$
DELIMITER ;

#delete class, classenrollees also delete
#check that quizzes also delete
DELIMITER $$
drop procedure if exists DeleteClass $$
create procedure DeleteClass (myClassId int
)
begin
delete from Classroom where classId = myClassId;
end $$
DELIMITER ;

#delete from classenrollees (without deleting entire class)
DELIMITER $$
drop procedure if exists DeleteClassEnrollee $$
create procedure DeleteClassEnrollee (myClassId int, myStudentId int 
)
begin
delete from ClassEnrollees where classId = myClassId AND studentId = myStudentId;
end $$
DELIMITER ;

#delete from question choice
DELIMITER $$
drop procedure if exists DeleteChoice $$
create procedure DeleteChoice (myChoiceId int)
begin
delete from QuestionChoices where choiceId = myChoiceId;
end $$
DELIMITER ;

#delete from UserAccounts (should also delete from instructors and students)
DELIMITER $$
drop procedure if exists DeleteUser $$
create procedure DeleteUser (myUserId int)
begin
delete from UserAccounts where userId = myUserId;
end $$
DELIMITER ;

#delete from quizzes table (should also delete questions and choices)
DELIMITER $$
drop procedure if exists DeleteQuiz $$
create procedure DeleteQuiz (myQuizId int
)
begin
delete from Quizzes where QuizId = myQuizId;
end $$
DELIMITER ;

/*MODIFY DATA IN TABLES*/

#edit question procedure

#edit objective procedure

#edit choices procedure

#edit quiz info

#edit student info-> there's some baggage here i fear

#edit instructor info-> there's some baggage here i fear

/*QUIZ FUNCTIONALITY*/

#GetQuizObjectives

#Student chooses learning objective
-- THESE CAN ONLY BE OBJECTIVES THAT ARE IN A QUIZ
DELIMITER $$
drop procedure if exists SelectStudentObjective $$
CREATE PROCEDURE SelectStudentObjective (
    IN myStudentId INT,
    IN myQuizId INT,
    IN myObjectiveName VARCHAR(200)
)
BEGIN
    DECLARE myObjectiveId INT;
    DECLARE numSelected INT;

-- we need to look up objective id since it is pk, but i dont want students to be picking things by id
    SELECT objectiveId INTO myObjectiveId
    FROM readingObjectives
    WHERE objectiveName = myObjectiveName
    LIMIT 1;

    IF myObjectiveId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Learning Objective not found.';
    END IF;

    SELECT COUNT(*) INTO numSelected
    FROM StudentObjectives
    WHERE studentId = myStudentId AND quizId = myQuizId;

    IF numSelected >= 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'You may only select up to 3 learning objectives for this quiz.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM StudentObjectives
        WHERE studentId = myStudentId AND quizId = myQuizId AND objectiveId = myObjectiveId
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'You have already selected this learning objective.';
    END IF;

    INSERT INTO StudentObjectives (studentId, quizId, objectiveId)
    VALUES (myStudentId, myQuizId, myObjectiveId);
END$$

DELIMITER ;

#student to move to next question
DELIMITER $$
drop procedure if exists GetNextQuestion $$
CREATE PROCEDURE GetNextQuestion (
    IN myQuizId INT,
    IN myStudentId INT
)
BEGIN
    SELECT q.questionId, q.questionText, qc.choiceId, qc.choiceLabel, qc.choiceText
    FROM Questions q
    JOIN QuestionChoices qc ON q.questionId = qc.questionId
    WHERE q.quizId = myQuizId
      AND q.questionId NOT IN (
          SELECT questionId
          FROM Attempts
          WHERE quizId = myQuizId AND studentId = myStudentId
      )
    ORDER BY q.questionId
    LIMIT 1;
END$$
DELIMITER ;

#submit answer one by one
DELIMITER $$
drop procedure if exists SubmitAnswer $$
CREATE PROCEDURE SubmitAnswer (
    IN myStudentId INT,
    IN myQuizId INT,
    IN myQuestionId INT,
    IN myChoiceId INT
)
BEGIN
    DECLARE correctId INT;
    DECLARE isCorrectVal BOOLEAN;

    -- find the correct answer
    SELECT correct_choice_id INTO correctId
    FROM Questions WHERE questionId = myQuestionId;

    -- check if its correct
    SET isCorrectVal = (myChoiceId = correctId);

    -- insert attempt
    INSERT INTO Attempts (studentId, quizId, questionId, chosenChoiceId, isCorrect, pointsEarned)
    VALUES (myStudentId, myQuizId, myQuestionId, myChoiceId, isCorrectVal, IF(isCorrectVal, 1, 0));

    -- return feedback immediately
    SELECT isCorrectVal AS isCorrect, correctId AS correctChoiceId;
END$$
DELIMITER ;

#submit quiz as a whole


-- =========================
-- GET VALUES BASED ON ATTRIBUTES
-- =========================

#get quiz score
DELIMITER $$
drop procedure if exists GetQuizScore $$
CREATE PROCEDURE GetQuizScore (
    IN myStudentId INT,
    IN myQuizId INT
)
BEGIN
    SELECT SUM(pointsEarned) AS totalScore,
           COUNT(*) AS totalQuestions,
           ROUND(SUM(pointsEarned) / COUNT(*) * 100, 2) AS percentage
    FROM Attempts
    WHERE studentId = myStudentId
      AND quizId = myQuizId;
END$$
DELIMITER ;

#search class enrollees by classId
DELIMITER $$
drop procedure if exists getEnrolleesByClass $$
create procedure getEnrolleesByClass(
myClassId int
)
BEGIN
select s.studentId, s.firstName, s.lastName 
from ClassEnrollees ce
join Students s on ce.studentId = s.studentId
where classId=myClassId;
END$$
DELIMITER ;

#Get all classes that a student is in
DELIMITER $$
drop procedure if exists getStudentsClasses $$
create procedure getStudentsClasses(
myStudentId int
)
BEGIN
SELECT 
        c.classId,
        c.className,
        c.firstName AS instructorFirstName,
        c.lastName AS instructorLastName
    FROM ClassEnrollees ce
    JOIN Classroom c ON ce.classId = c.classId
    WHERE ce.studentId = myStudentId;
END$$
DELIMITER ;

#getStudentBadges
DELIMITER $$
drop procedure if exists getStudentBadges $$
create procedure getStudentBadges(
myStudentId int
)
BEGIN
SELECT 
        b.badgeName
    FROM studentBadges sb
    JOIN Badges b ON sb.badgeId= b.badgeId
    WHERE sb.studentId = myStudentId;
END$$
DELIMITER ;

#getInstructorClasses
DELIMITER $$
drop procedure if exists getInstructorClasses $$
create procedure getInstructorClasses(
myInstructorId int
)
BEGIN
SELECT 
        c.classId,
        c.className,
        c.firstName AS instructorFirstName,
        c.lastName AS instructorLastName
    FROM Classroom c
    WHERE c.instructorId = myInstructorId;
END$$
DELIMITER ;

#getQuizzesByClass
#getInstructorClasses
DELIMITER $$
drop procedure if exists getQuizzesByClass $$
create procedure getQuizzesByClass(
myclassId int
)
BEGIN
SELECT 
        q.quizName
    FROM Quizzes q
    WHERE q.classId = myClassId;
END$$
DELIMITER ;
