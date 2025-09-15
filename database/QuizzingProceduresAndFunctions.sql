USE QuizzingDB;

/*QUESTION PROCEDURES*/
#make sure question isn't a repeat when inserting 
DELIMITER $$
DROP FUNCTION IF EXISTS doesQuestionExist;$$
CREATE FUNCTION doesQuestionExist(myQuestionName TEXT)
RETURNS BOOLEAN DETERMINISTIC
BEGIN
declare result int;
SELECT COUNT(*) INTO result FROM Questions WHERE question_text = myQuestionText;
    RETURN (result > 0);
    END;$$
DELIMITER ;

#insert a question to a quiz
DROP PROCEDURE IF EXISTS InsertNewQuestion;
delimiter $$
CREATE PROCEDURE InsertNewQuestion (
myQuestionText text,
myDifficulty enum("easy","medium", "hard"),
myCorrectQ char(1),
myChoice_a VARCHAR(300),
myChoice_b VARCHAR(300),
myChoice_c VARCHAR(300),
myChoice_d VARCHAR(300),
myCorrectAnswer CHAR(1),
myObjectiveId int)
BEGIN
IF NOT doesQuestionExist(myQuestionText) THEN
        INSERT INTO Questions (question_text, difficulty)
        VALUES (myQuestionText, myDifficulty);
        
        SET newQuestionId = LAST_INSERT_ID();
        
        INSERT INTO QuestionChoices (question_id, choice_label, choice_text)
        VALUES (newQuestionId, 'A', myChoiceA),
               (newQuestionId, 'B', myChoiceB),
               (newQuestionId, 'C', myChoiceC),
               (newQuestionId, 'D', myChoiceD);
               
	    #assign correct choice
        UPDATE Questions
        SET correct_choice_id = (
            SELECT choice_id FROM QuestionChoices 
            WHERE question_id = newQuestionId AND choice_label = myCorrectLabel
        )
        WHERE question_id = newQuestionId;
        INSERT INTO QuestionObjectives (question_id, objective_id)
        VALUES (newQuestionId, myObjectiveId);
    END IF;
END $$
delimiter ;

#assign objective(s) to question 
DELIMITER $$
DROP PROCEDURE IF EXISTS AssignObjective $$
CREATE PROCEDURE AssignObjective (
)
BEGIN
END $$
DELIMITER ;

#delete question if typo
DELIMITER $$
DROP PROCEDURE IF EXISTS DeleteQuestion $$
CREATE PROCEDURE DeleteQuestion (
)
BEGIN
END $$
DELIMITER ;

/*QUIZ PROCEDURES*/
#to move to next question
DELIMITER $$
DROP PROCEDURE IF EXISTS GetNextQuestion $$
CREATE PROCEDURE GetNextQuestion (
)
BEGIN
END $$
DELIMITER ;

#for storing results
DELIMITER $$
DROP PROCEDURE IF EXISTS RecordAttempt $$
CREATE PROCEDURE RecordAttempt (
)
BEGIN
END $$
DELIMITER ;

/*Analytics*/
#for CSV export
DELIMITER $$
DROP PROCEDURE IF EXISTS GetAttemptResults $$
CREATE PROCEDURE GetAttemptResults (
)
BEGIN
END $$
DELIMITER ;

/*Gamification*/
#for assigning badges
DELIMITER $$
DROP PROCEDURE IF EXISTS AssignBadge $$
CREATE PROCEDURE AssignBadge (
)
BEGIN
END $$
DELIMITER ;

/*STUDENT PROCEDURES*/ 
#check if a student already exists by email
DELIMITER $$
DROP FUNCTION IF EXISTS doesStudentExist $$
CREATE FUNCTION doesStudentExist(myEmail VARCHAR(150))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE result INT;
    SELECT COUNT(*) INTO result 
    FROM Users 
    WHERE email = myEmail AND is_instructor = FALSE;
    RETURN (result > 0);
END $$
DELIMITER ;

#insert a student
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewStudent $$
CREATE PROCEDURE InsertNewStudent (
    IN myStudentName VARCHAR(100), 
    IN myEmail VARCHAR(150), 
    IN myPasswordHash VARCHAR(255)
)
BEGIN
    IF NOT doesStudentExist(myEmail) THEN
        INSERT INTO Users (name, email, password_hash, is_instructor)
        VALUES (myStudentName, myEmail, myPasswordHash, FALSE);
    END IF;
END $$
DELIMITER ;

/*INSTRUCTOR PROCEDURES*/ 
#check if an instructor already exists by email
DELIMITER $$
DROP FUNCTION IF EXISTS doesStudentExist $$
CREATE FUNCTION doesStudentExist(myEmail VARCHAR(150))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE result INT;
    SELECT COUNT(*) INTO result 
    FROM Users 
    WHERE email = myEmail AND is_instructor = FALSE;
    RETURN (result > 0);
END $$
DELIMITER ;

#insert an instructor
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewStudent $$
CREATE PROCEDURE InsertNewStudent (
    IN myStudentName VARCHAR(100), 
    IN myEmail VARCHAR(150), 
    IN myPasswordHash VARCHAR(255)
)
BEGIN
    IF NOT doesStudentExist(myEmail) THEN
        INSERT INTO Users (name, email, password_hash, is_instructor)
        VALUES (myStudentName, myEmail, myPasswordHash, FALSE);
    END IF;
END $$
DELIMITER ;

/*ACCOUNT PROCEDURES*/
DELIMITER $$
DROP PROCEDURE IF EXISTS LoginUser $$
CREATE PROCEDURE LoginUser (
)
BEGIN
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS DeleteUser $$
CREATE PROCEDURE DeleteUser (
)
BEGIN
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS ResetPassword $$
CREATE PROCEDURE ResetPassword (
)
BEGIN
END $$
DELIMITER ;

