#Cohesive set up
USE QuizzingDb;

#register new user- instructor and student
#TESTED: GOOD
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewUser $$
CREATE PROCEDURE InsertNewUser (
	IN myGoogleSub VARCHAR(255),
    IN myEmail VARCHAR(150),
    IN myIsInstructor BOOLEAN,
    IN myMajor VARCHAR(100),        
    IN mySchoolSubject VARCHAR(50),
    IN myFirstName varchar(20),
    IN myLastName varchar(50)
)
BEGIN
    DECLARE newUserId INT;
    INSERT INTO UserAccounts (googleSub, email, isInstructor, firstName, lastName)
    VALUES (myGoogleSub, myEmail, myIsInstructor, myFirstName, myLastName);
    -- gets the auto-incremented ID from the last insert
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

#select user id from email
DELIMITER $$
DROP PROCEDURE IF EXISTS LookupUser $$
CREATE PROCEDURE LookupUser (IN myEmail VARCHAR(150))
BEGIN
    DECLARE foundUserId INT;
    DECLARE isInstructorFlag BOOLEAN;
    SELECT userId, isInstructor
    INTO foundUserId, isInstructorFlag
    FROM UserAccounts
    WHERE email = myEmail;
    IF foundUserId IS NULL THEN
        SELECT NULL AS userId; -- consistent column name
    ELSEIF isInstructorFlag = TRUE THEN
        SELECT instructorId AS userId
        FROM Instructors
        WHERE email = myEmail;
    ELSE
        SELECT studentId AS userId
        FROM Students
        WHERE email = myEmail;
    END IF;
END $$
DELIMITER ;

#gets id and email from the sub (called at the beginning of each page load)
DELIMITER $$
DROP PROCEDURE IF EXISTS LookupUserBySub $$
CREATE PROCEDURE LookupUserBySub (IN myGoogleSub VARCHAR(255))
BEGIN
    SELECT 
        ua.userId,
        ua.email,
        CASE WHEN ua.isInstructor = TRUE THEN i.instructorId ELSE s.studentId END AS mappedId
    FROM UserAccounts ua
    LEFT JOIN Instructors i ON i.instructorId = ua.userId
    LEFT JOIN Students s ON s.studentId = ua.userId
    WHERE ua.googleSub = myGoogleSub;
END $$
DELIMITER ;


#create new class- instructor
#TESTED: GOOD
#DAL: Done
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

    SELECT instructorId, firstName, lastName
    INTO instructorIdFound, instructorFirstName, instructorLastName
    FROM Instructors
    WHERE email = myEmail
    LIMIT 1;
    -- if no instructor
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
#DAL: Done
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
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS EnrollStudent $$
CREATE PROCEDURE EnrollStudent (
    IN myClassId INT,
    IN myEmail VARCHAR(150)
)
BEGIN
    DECLARE vUserId INT;
    DECLARE vStudentId INT;
    -- look up the userId by email
    SELECT userId INTO vUserId
    FROM UserAccounts
    WHERE email = myEmail;
    -- if no user found put an error
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
    IF EXISTS (
        SELECT 1 FROM ClassEnrollees
        WHERE classId = myClassId AND studentId = vStudentId
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Student is already enrolled in this class.';
    ELSE
        INSERT INTO ClassEnrollees (classId, studentId)
        VALUES (myClassId, vStudentId);
    END IF;
END $$
DELIMITER ;

#list class enrollees
#TESTED: GOOD
#DAL: Done
DELIMITER $$
drop procedure if exists getEnrolleesByClass $$
create procedure getEnrolleesByClass(
myClassId int
)
BEGIN
select s.studentId, s.firstName, s.email, s.lastName 
from ClassEnrollees ce
join Students s on ce.studentId = s.studentId
where classId=myClassId;
END$$
DELIMITER ;

#display student classes
#TESTED: GOOD
#DAL: Done
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
#TESTED: GOOD
#DAL: Done
delimiter $$
drop procedure if exists InsertNewReading;
create procedure InsertNewReading (
    myInstructorId int,
    myClassId int,
    myReadingName varchar(255)
) 
begin
    insert into Readings (readingName, instructorId, classId)
        values (myReadingName, myInstructorId, myClassId);
    select LAST_INSERT_ID() as readingId;
end $$
delimiter ;

#insert reading objectives into db table
#TESTED: GOOD
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewReadingObjective $$
CREATE PROCEDURE InsertNewReadingObjective (
    IN myReadingId INT,
    IN myClassId INT,
    IN myObjectiveName VARCHAR(255)
)
BEGIN
    INSERT INTO ReadingObjectives (readingId, classId, objectiveName)
    VALUES (myReadingId, myClassId, myObjectiveName);
    SELECT LAST_INSERT_ID() AS objectiveId;
END $$
DELIMITER ;

#Get readings by class
#TESTED: GOOD
#DAL: Done
DELIMITER $$
drop procedure if exists getReadingsByClass $$
create procedure getReadingsByClass(
myclassId int
)
BEGIN
SELECT 
        r.readingId, r.readingName, r.filePath
    FROM Readings r
    WHERE r.classId = myClassId;
END$$
DELIMITER ;

#getReadingObjectives
DELIMITER $$
drop procedure if exists getReadingObjectives $$
create procedure getReadingObjectives(
myReadingId int
)
BEGIN
SELECT 
        ro.objectiveId, ro.objectiveName
    FROM ReadingObjectives ro
    WHERE ro.readingId = myReadingId;
END$$
DELIMITER ;

#create new quiz- just inserts basic quiz info
#TESTED: GOOD
#Dal: Done
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

#create basic quiz
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertQuiz$$
CREATE PROCEDURE InsertQuiz(
    IN myInstructorId INT,
    IN myClassId INT
)
BEGIN
    INSERT INTO Quizzes (instructorId, classId)
    VALUES (myInstructorId, myClassId);
    -- Return the auto-generated quizId
    SELECT LAST_INSERT_ID() AS quizId;
END $$
DELIMITER ;

#updates quiz name (blank quiz is created first)
DELIMITER $$
DROP PROCEDURE IF EXISTS updateQuizName $$
CREATE PROCEDURE updateQuizName (
    IN p_quizId INT,
    IN p_quizName VARCHAR(255)
)
BEGIN
    UPDATE Quizzes
    SET quizName = p_quizName
    WHERE quizId = p_quizId;
END $$
DELIMITER ;

#assign readings to quiz
#TESTED: GOOD
#DAl: Done
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
#TESTED: GOOD
#DAL: Done
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
end $$
delimiter ;

#display quizzes inside of classes
#TESTED: GOOD
#DAL: Done
DELIMITER $$
drop procedure if exists getQuizzesByClass $$
create procedure getQuizzesByClass(
myclassId int
)
BEGIN
SELECT 
        q.quizName, q.quizId
    FROM Quizzes q
    WHERE q.classId = myClassId;
END$$
DELIMITER ;

#get all questions in a quiz
#TESTED: GOOD
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS getQuizQuestions $$
CREATE PROCEDURE getQuizQuestions (
    IN myQuizId INT
)
BEGIN
    SELECT
        q.questionId,
        q.questionNumber,
        q.difficulty,
        q.questionText,
        ro.objectiveId,
        ro.objectiveName AS learningObjective,
        qc.choiceId,
        qc.choiceLabel,
        q.correct_choice_id,
        qc.choiceText
    FROM Questions q
    JOIN QuestionObjectives qo 
        ON q.questionId = qo.questionId
    JOIN ReadingObjectives ro 
        ON qo.objectiveId = ro.objectiveId
    JOIN QuestionChoices qc 
        ON q.questionId = qc.questionId
    WHERE q.quizId = myQuizId
    ORDER BY q.questionNumber, qc.choiceLabel;
END $$
DELIMITER ;

#display relavant objectives based on quiz
#TESTED: GOOD
#DAL: Done
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

#student select objectives- inserts into a table
#TESTED: GOOD
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS SelectStudentObjective $$
CREATE PROCEDURE SelectStudentObjective (
    IN myStudentId INT,
    IN myQuizId INT,
    IN myObjectiveId int
)
BEGIN
    DECLARE numSelected INT;

    IF myObjectiveId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Learning Objective not found.';
    END IF;
    -- count how many objectives the student has already chosen for this quiz
    SELECT COUNT(*) INTO numSelected
    FROM StudentObjectives
    WHERE studentId = myStudentId AND quizId = myQuizId;
    IF numSelected >= 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'You may only select up to 3 learning objectives for this quiz.';
    END IF;
    -- prevent duplicate selection
    IF EXISTS (-- prevent duplicate selection
        SELECT 1 FROM StudentObjectives
        WHERE studentId = myStudentId 
          AND quizId = myQuizId 
          AND objectiveId = myObjectiveId
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'You have already selected this learning objective.';
    END IF;

    -- insert the selected objective
    INSERT INTO StudentObjectives (studentId, quizId, objectiveId)
    VALUES (myStudentId, myQuizId, myObjectiveId);
END$$
DELIMITER ;

#get student's objectives
DELIMITER $$
DROP PROCEDURE IF EXISTS getStudentObjectives $$
CREATE PROCEDURE getStudentObjectives(
    IN myStudentId INT
)
BEGIN
    SELECT ro.objectiveName, ro.ObjectiveId
    FROM StudentObjectives so
    INNER JOIN ReadingObjectives ro 
        ON so.objectiveId = ro.objectiveId
    WHERE so.studentId = myStudentId;
END $$
DELIMITER ;

#reset these after every attempt
DELIMITER $$
DROP PROCEDURE IF EXISTS deleteStudentObjectives $$
CREATE PROCEDURE deleteStudentObjectives(
    IN myStudentId INT
)
BEGIN
    DELETE FROM StudentObjectives
    WHERE studentId = myStudentId;
END $$
DELIMITER ;

#student gets next question
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS GetNextQuestion $$
CREATE PROCEDURE GetNextQuestion (
    IN myQuizId INT,
    IN myStudentId INT
)
BEGIN
    DECLARE nextQuestionId INT;
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
#DAL: Done
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
#DAL: Done
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

#assign badge- when points in profile reaches a threshold (100, 250, 500, 750, 100)
#DAL: Done
DELIMITER $$
DROP PROCEDURE IF EXISTS assignBadge $$
CREATE PROCEDURE assignBadge(IN myStudentId INT)
BEGIN
    DECLARE myTotalPoints INT;
    DECLARE myBadgeId INT;
    DECLARE myBadgeName VARCHAR(100);
    DECLARE badgeBefore VARCHAR(100);
    -- Get current badge for comparison
    SELECT badge INTO badgeBefore
    FROM Students
    WHERE studentId = myStudentId;
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
        -- Add to StudentBadges if they don't already have it
        INSERT IGNORE INTO StudentBadges (studentId, badgeId)
        VALUES (myStudentId, myBadgeId);
        -- Update Students.badge if it's a NEW badge
        IF badgeBefore IS NULL OR badgeBefore <> myBadgeName THEN
            UPDATE Students
            SET badge = myBadgeName
            WHERE studentId = myStudentId;
            -- Return the new badge name
            SELECT myBadgeName AS newBadge;
        ELSE
            -- No new badge earned
            SELECT NULL AS newBadge;
        END IF;
    ELSE
        -- No qualifying badge
        SELECT NULL AS newBadge;
    END IF;
END$$
DELIMITER ;

#display student badges
#DAL: Done
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

DELIMITER $$
drop procedure if exists DeleteReading $$
create procedure DeleteReading (myReadingId int
)
begin
delete from Readings where readingId = myReadingId;
end $$
DELIMITER ;

#checks if user can create a quiz
DELIMITER $$
drop procedure if exists canCreateQuiz $$
CREATE PROCEDURE canCreateQuiz(
    IN myUserId INT,
    IN myClassId INT
)
BEGIN
    DECLARE isEligible INT DEFAULT 0;
    SELECT 
        CASE
            WHEN EXISTS (SELECT 1 FROM Instructors WHERE instructorId = myUserId)
                 OR EXISTS (SELECT 1 FROM Classroom WHERE classId = myClassId AND instructorId = myUserId)
            THEN 1
            ELSE 0
        END
    INTO isEligible;
    SELECT isEligible AS canCreate;
END$$
DELIMITER ;

DELIMITER $$
drop procedure if exists isInstructorCheck $$
CREATE PROCEDURE isInstructorCheck(
    IN myUserId INT
)
BEGIN
DECLARE instructorFlag BOOLEAN;

    SELECT isInstructor
    INTO instructorFlag
    FROM UserAccounts
    WHERE userId = myUserId;

    SELECT instructorFlag AS isInstructor;
END$$
DELIMITER ;

#BEGINS attempt session- right after they choose their objective
DELIMITER $$
DROP PROCEDURE IF EXISTS StartAttemptSession $$
CREATE PROCEDURE StartAttemptSession(
    IN myStudentId INT,
    IN myQuizId INT,
    IN myObjectiveId INT
)
BEGIN
    INSERT INTO AttemptSessions (studentId, quizId, objectiveId)
    VALUES (myStudentId, myQuizId, myObjectiveId);

    SELECT LAST_INSERT_ID() AS sessionId;
END$$
DELIMITER ;

#saves each answer
DELIMITER $$
DROP PROCEDURE IF EXISTS SaveStudentAnswer $$
CREATE PROCEDURE SaveStudentAnswer(
    IN mySessionId INT,
    IN myQuestionId INT,
    IN myChosenChoiceId INT
)
BEGIN
    DECLARE correctChoiceId INT;
    DECLARE correctLetter CHAR(1);
    DECLARE chosenLetter CHAR(1);
    DECLARE isCorrect BOOLEAN;
    -- get correct choice ID and label
    SELECT qc.choiceId, qc.choiceLabel
    INTO correctChoiceId, correctLetter
    FROM Questions q
    JOIN QuestionChoices qc ON q.correct_choice_id = qc.choiceId
    WHERE q.questionId = myQuestionId;
    -- get chosen letter
    SELECT choiceLabel INTO chosenLetter
    FROM QuestionChoices
    WHERE choiceId = myChosenChoiceId;

    -- checkcorrectnss
    SET isCorrect = (chosenLetter = correctLetter);
    -- insert attempt answer
    INSERT INTO AttemptAnswers (
        sessionId, questionId, chosenChoiceId, chosenLetter,
        correctLetter, isCorrect, pointsEarned
    )
    VALUES (
        mySessionId, myQuestionId, myChosenChoiceId, chosenLetter,
        correctLetter, isCorrect, IF(isCorrect, 1, 0)
    );
    -- points
    IF isCorrect THEN
        UPDATE Students
        SET totalPoints = totalPoints + 5
        WHERE studentId = (
            SELECT studentId FROM AttemptSessions WHERE sessionId = mySessionId
        );
    END IF;
END$$
DELIMITER ;

#ENDS attempt session
DELIMITER $$
DROP PROCEDURE IF EXISTS FinalizeAttemptSession $$
CREATE PROCEDURE FinalizeAttemptSession(
    IN mySessionId INT
)
BEGIN
    DECLARE totalCorrect INT;
    DECLARE totalQuestions INT;
    DECLARE percent DECIMAL(5,2);
    -- total correct answers
    SELECT SUM(isCorrect) INTO totalCorrect
    FROM AttemptAnswers
    WHERE sessionId = mySessionId;
    -- number of answered questions
    SELECT COUNT(*) INTO totalQuestions
    FROM AttemptAnswers
    WHERE sessionId = mySessionId;

    IF totalQuestions = 0 THEN
        SET percent = 0.00;
    ELSE
        SET percent = (totalCorrect / totalQuestions) * 100;
    END IF;

    UPDATE AttemptSessions
    SET score = totalCorrect,
        percentage = percent
    WHERE sessionId = mySessionId;
END$$
DELIMITER ;

#gets the session results
DELIMITER $$
DROP PROCEDURE IF EXISTS GetSessionResults $$
CREATE PROCEDURE GetSessionResults(
    IN mySessionId INT
)
BEGIN
    SELECT * FROM AttemptSessions WHERE sessionId = mySessionId;

    SELECT aa.*, q.questionText
    FROM AttemptAnswers aa
    JOIN Questions q ON q.questionId = aa.questionId
    WHERE aa.sessionId = mySessionId;
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS GetLatestSessionId $$
CREATE PROCEDURE GetLatestSessionId(
    IN myStudentId INT
)
BEGIN
    SELECT sessionId
    FROM AttemptSessions
    WHERE studentId = myStudentId
    ORDER BY sessionId DESC
    LIMIT 1;
END$$
DELIMITER ;