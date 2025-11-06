#Cohesive set up
USE QuizzingDb;

#register new user- instructor and student
#TESTED: GOOD
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

#create new class- instructor
#TESTED: GOOD
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

#display instructor's classes
#TESTED: GOOD
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

#enroll student in class
#TESTED: GOOD
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

#list class enrollees
#TESTED: GOOD

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

#display student classes
#TESTED: GOOD
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

#insert reading inside class module
delimiter $$
drop procedure if exists InsertNewReading;
create procedure InsertNewReading (
myInstructorId int,
myClassId int,
myReadingName varchar(255),
myFilePath varchar(500)
) 
begin

DECLARE newReadingId INT;
 
insert into Readings (readingId, readingName, filePath, instructorId, classId)
    values (newReadingId, myReadingName, myFilePath, myInstructorId, myClassId);
    
SET newReadingId = LAST_INSERT_ID();
end $$
delimiter ;

#insert reading objectives into db table
delimiter $$
drop procedure if exists InsertNewReadingObjective;
create procedure InsertNewReadingObjective (
myReadingId int,
myClassId int,
myObjectiveName varchar(255)
) 
begin

DECLARE newObjectiveId INT;
 
insert into ReadingObjectives (objectiveId, readingId, classId, objectiveName)
    values (newObjectiveId, myReadingId, myClassId, myObjectiveName);
    
SET newObjectiveId = LAST_INSERT_ID();
end $$
delimiter ;

#Get readings by class

#create new quiz- just inserts basic quiz info
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewQuiz;
CREATE PROCEDURE InsertNewQuiz(
    IN myQuizName VARCHAR(100),
    IN myInstructorId INT,
    IN myClassId INT,
    OUT newQuizId INT
)
BEGIN
    INSERT INTO Quizzes (quizName, instructorId, classId)
    VALUES (myQuizName, myInstructorId, myClassId);
    
    SET newQuizId = LAST_INSERT_ID();
END $$
DELIMITER ;


#assign readings to quiz
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertQuizReading;
CREATE PROCEDURE InsertQuizReading(
    IN myQuizId INT,
    IN myReadingId INT
)
BEGIN
    INSERT INTO QuizReadings (quizId, readingId)
    VALUES (myQuizId, myReadingId);
END $$
DELIMITER ;

#create new question + their objectives - inserts questions into a quiz (can be called multiple times)
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

#display quizzes inside of classes
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

#display relavant objectives based on quiz
DELIMITER $$
drop procedure if exists getQuizObjectives $$
create procedure getQuizObjectives(
myQuizId int
)
BEGIN
SELECT o.objectiveId, o.objectiveName
FROM ReadingObjectives o
JOIN Readings r ON o.readingId = r.readingId
JOIN QuizReadings qr ON qr.readingId = r.readingId
WHERE qr.quizId = myQuizId;
END$$
DELIMITER ;

#student select objectives- inserts into temporary table
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

#student gets next question
DELIMITER $$
DROP PROCEDURE IF EXISTS GetNextQuestion $$
CREATE PROCEDURE GetNextQuestion (
    IN myQuizId INT,
    IN myStudentId INT
)
BEGIN
    DECLARE nextQuestionId INT;

    -- Find the next unanswered question
    SELECT q.questionId
    INTO nextQuestionId
    FROM Questions q
    WHERE q.quizId = myQuizId
      AND q.questionId NOT IN (
          SELECT questionId FROM Attempts
          WHERE quizId = myQuizId AND studentId = myStudentId
      )
    ORDER BY q.questionId
    LIMIT 1;

    -- If no more questions
    IF nextQuestionId IS NULL THEN
        SELECT NULL AS questionId, NULL AS questionText, NULL AS choiceId, NULL AS choiceLabel, NULL AS choiceText;
    ELSE
        -- Return the question and all its choices
        SELECT q.questionId, q.questionText, qc.choiceId, qc.choiceLabel, qc.choiceText
        FROM Questions q
        JOIN QuestionChoices qc ON q.questionId = qc.questionId
        WHERE q.questionId = nextQuestionId;
    END IF;
END$$
DELIMITER ;

#student submits answer- gets immediate feedback
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

#get full quiz score after finishing quiz- automatically adds points to their profile
DELIMITER $$

DROP PROCEDURE IF EXISTS GetQuizScore $$

CREATE PROCEDURE GetQuizScore (
    IN myStudentId INT,
    IN myQuizId INT
)
BEGIN
    DECLARE quizPoints INT DEFAULT 0;

    -- Calculate total quiz points for this attempt
    SELECT COALESCE(SUM(pointsEarned), 0)
    INTO quizPoints
    FROM Attempts
    WHERE studentId = myStudentId
      AND quizId = myQuizId;

    -- Add the quiz points to the student's totalPoints
    UPDATE Students
    SET totalPoints = COALESCE(totalPoints, 0) + quizPoints
    WHERE studentId = myStudentId;

    -- Return the results 
    SELECT 
        quizPoints AS totalScore,
        COUNT(*) AS totalQuestions,
        ROUND((quizPoints / COUNT(*)) * 100, 2) AS percentage
    FROM Attempts
    WHERE studentId = myStudentId
      AND quizId = myQuizId;
END$$

DELIMITER ;


#display profile points

#assign badge- when points in profile reaches a threshold (100, 250, 500, 750, 100)
DELIMITER $$

DROP PROCEDURE IF EXISTS assignBadge $$
CREATE PROCEDURE assignBadge(IN myStudentId INT)
BEGIN
    DECLARE myTotalPoints INT;
    DECLARE myBadgeId INT;
    DECLARE myBadgeName VARCHAR(100);

    -- Get the student's total points
    SELECT totalPoints INTO myTotalPoints
    FROM Students
    WHERE studentId = myStudentId;

    -- Find the highest badge they qualify for
    SELECT badgeId, badgeName
    INTO myBadgeId, myBadgeName
    FROM Badges
    WHERE pointThreshold <= myTotalPoints
    ORDER BY pointThreshold DESC
    LIMIT 1;

    -- Only proceed if they qualify for at least one badge
    IF myBadgeId IS NOT NULL THEN

        -- Insert into StudentBadges if they don’t already have this badge
        INSERT IGNORE INTO StudentBadges (studentId, badgeId)
        VALUES (myStudentId, myBadgeId);

        -- Update their current badge in Students table
        UPDATE Students
        SET badge = myBadgeName
        WHERE studentId = myStudentId;

    END IF;
END$$

DELIMITER ;


#display student badges
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

#display all badges

#instructor removes student from class
DELIMITER $$
drop procedure if exists DeleteClassEnrollee $$
create procedure DeleteClassEnrollee (myClassId int, myStudentId int 
)
begin
delete from ClassEnrollees where classId = myClassId AND studentId = myStudentId;
end $$
DELIMITER ;

#user deletes their account
DELIMITER $$
drop procedure if exists DeleteUser $$
create procedure DeleteUser (myUserId int)
begin
delete from UserAccounts where userId = myUserId;
end $$
DELIMITER ;

#instructor deletes their class
DELIMITER $$
drop procedure if exists DeleteClass $$
create procedure DeleteClass (myClassId int
)
begin
delete from Classroom where classId = myClassId;
end $$
DELIMITER ;

#instructor deletes their quiz
DELIMITER $$
drop procedure if exists DeleteQuiz $$
create procedure DeleteQuiz (myQuizId int
)
begin
delete from Quizzes where QuizId = myQuizId;
end $$
DELIMITER ;



