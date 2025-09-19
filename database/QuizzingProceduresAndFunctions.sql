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

#check if user is student

#check if user is instructor

#check if student answer is correct

/*Procedures*/

#insert student to student table automatically

#insert instructor to instructor table automatically

#class creation procedure

#quiz creation procedure
delimiter $$
drop procedure if exists InsertNewQuiz;
create procedure InsertNewQuiz (
myQuizId int,
myQuizName varchar(100),
myUserId int) -- add class id, set it by getting classid automatically?
begin
declare my_current_time timestamp;
set my_current_time = current_timestamp;

insert into Quizzes (quizId, quizName, userId, created_at)
    values (myQuizId, myQuizName, myUserId, my_current_time);
end $$
delimeter ;

#insert a question to a quiz with choices and objective
delimiter $$
DROP PROCEDURE IF EXISTS InsertNewQuestion $$
CREATE PROCEDURE InsertNewQuestion (
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
        insert into Questions (questionText, difficulty, quizId)
        values (myQuestionText, myDifficulty, myQuizId);
        
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

#delete question if typo, choices/objectives will also delete
DELIMITER $$
drop procedure if exists DeleteQuestion $$
create procedure DeleteQuestion (myQuestionId int
)
begin
delete from Questions where question_id = myQuestionId;
end $$
DELIMITER ;

#modify question

#modify objective

#modify choices

#student to move to next question
DELIMITER $$
drop procedure if exists GetNextQuestion $$
create procedure GetNextQuestion (myQuizId int, myStudentId int
)
BEGIN
-- this will get the first question they haven't answered yet
-- Return first unanswered question in this quiz
    select q.questionId, q.questionText, qc.choiceId, qc.choiceLabel, qc.choiceText
    from Questions q
    join QuestionChoices qc ON q.questionId = qc.questionId
    where q.quizId = myQuizId
      and q.questionId not in (
          select questionId from Attempts 
          where quizId = myQuizId and studentId = myStudentId
      )
      limit 1; -- just return one
end $$
DELIMITER ;

#insert a student  to student table, automatically called when user is flagged as student
DELIMITER $$
DROP PROCEDURE IF EXISTS InsertNewStudent $$
CREATE PROCEDURE InsertNewStudent (
    IN myStudentId VARCHAR(100)
)
BEGIN
    IF NOT doesUserExist(myEmail) THEN
        INSERT INTO Students (studentId, badge, totalPoints)
        VALUES (myStudentId, null, 0);
        
        
    END IF;
END $$
DELIMITER ;

#procedure for adding a student to a class