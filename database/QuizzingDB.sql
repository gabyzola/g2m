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

CREATE TABLE Students (
studentId int primary key,
badge VARCHAR(100), #i will eventually make a bank of badges
totalPoints int,
foreign key (studentId) references UserAccounts(userId) on delete cascade
);

#will need to add to this! maybe?
CREATE TABLE Instructors (
instructorId int primary key,
schoolSubject varchar(50),
foreign key (instructorId) references UserAccounts(userId) on delete cascade
);

#im thinking that instead of useraccounts maybe the foreign key should link instructors table?
CREATE TABLE Classroom (
classId serial primary key,
className varchar(100) not null,
instructorId int not null,
created_at timestamp default current_timestamp,
foreign key (instructorId) references UserAccounts(userId) on delete cascade
);

#should foreign key be linked to students table and not user accounts?
CREATE TABLE ClassEnrollees (
classId int not null,
studentId int not null,
enrolled_at timestamp default current_timestamp,
primary key (classId, studentId),
foreign key (classId) references Classroom(classId) on delete cascade,
foreign key (studentId) references Students(studentId) on delete cascade
);

CREATE TABLE Quizzes (
    quizId serial primary key,
    quizName varchar(100),
    instructorId INT, #instructor who created it
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    classId int,
    foreign key (userId) references Instructors(instructorId) on delete cascade,
    foreign key (classId) references Classroom(classId) on delete cascade
);

CREATE TABLE Questions (
    questionId INT PRIMARY KEY AUTO_INCREMENT,
    questionText TEXT NOT NULL,
    quizId int not null,
    difficulty ENUM('easy','medium','hard') NOT NULL,
    correct_choice_id INT, #tie in to the correct option
    foreign key (quizId) references Quizzes(quizId) on delete cascade, 
    foreign key (correct_choice_id) references QuestionChoices(choiceId)
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
    studentId INT REFERENCES Students(studentId),
    quizId INT REFERENCES Quizzes(quizId),
    questionId INT REFERENCES Questions(questionId),
    chosenChoiceId INT REFERENCES QuestionChoices(choiceId),
    isCorrect BOOLEAN,
    pointsEarned INT DEFAULT 0,
    attemptTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);