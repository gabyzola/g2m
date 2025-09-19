DROP DATABASE IF EXISTS QuizzingDB;
CREATE DATABASE QuizzingDB;

DROP TABLE IF EXISTS Attempts; #tracks student attemps
DROP TABLE IF EXISTS Questions; #stores questions (input from professor)
DROP TABLE IF EXISTS LearningObjectives; #stores learning objectives (input from student or professor)
DROP TABLE IF EXISTS Students; #keeps track of students and accounts
DROP TABLE IF EXISTS Archives; #past information

CREATE TABLE UserAccounts (
    userId SERIAL PRIMARY KEY,              
    googleId VARCHAR(255) unique not null, #verify what this will look like 
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,  #merrimack email
    isInstructor BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastLogin TIMESTAMP
);

#important: student may have multiple instructors
#important: student may belong to more than one class
CREATE TABLE Students (
studentId VARCHAR(100) UNIQUE NOT NULL,
badge VARCHAR(100), #i will eventually make a bank of badges
totalPoints int,
foreign key (studentId) references UserAccounts(user_id) on delete cascade
);

#will need to add to this! maybe?
CREATE TABLE Instructors (
username varchar(100) unique not null,
foreign key (username) references UserAccounts on delete cascade
);

#CREATE TABLE Classroom ();

CREATE TABLE Quizzes (
    quizId int primary key,
    quizName varchar(100),
    userId INT, #instructor who created it
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    classId int,
    foreign key (classId) references Class(classId)
);

CREATE TABLE Questions (
    questionId INT PRIMARY KEY AUTO_INCREMENT,
    questionText TEXT NOT NULL,
    quizId int not null,
    difficulty ENUM('easy','medium','hard') NOT NULL,
    correct_choice_id INT, #tie in to the correct option
    foreign key (quizId) references Quizzes(quizId) on delete cascade #verify delete cascade
);


CREATE TABLE LearningObjectives (
    objectiveId INT PRIMARY KEY AUTO_INCREMENT,
    objectiveName VARCHAR(200) NOT NULL, #i will eventually just make a bank of objectives
    objDescript TEXT
);

CREATE TABLE QuestionObjectives (
    questionId INT,
    objectiveId INT,
    PRIMARY KEY (questionId, objectiveId),
    FOREIGN KEY (questionId) REFERENCES Questions(questionId),
    FOREIGN KEY (objectiveId) REFERENCES LearningObjectives(objectiveId)
);


CREATE TABLE QuestionChoices (
    choiceId INT PRIMARY KEY AUTO_INCREMENT,
    questionId INT,
    choiceLabel CHAR(1), #A, B, C, D
    choiceText VARCHAR(300) NOT NULL,
    FOREIGN KEY (questionId) REFERENCES Questions(questionId) on delete cascade
);


CREATE TABLE Attempts (
    attemptId SERIAL PRIMARY KEY,
    studentId INT REFERENCES Users(userId),
    quizId INT REFERENCES Quizzes(quizId),
    questionId INT REFERENCES Questions(questionId),
    chosenChoiceId INT REFERENCES QuestionChoices(choiceId),
    isCorrect BOOLEAN,
    pointsEarned INT DEFAULT 0,
    attemptTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);