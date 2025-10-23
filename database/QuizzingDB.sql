DROP DATABASE IF EXISTS QuizzingDB;
CREATE DATABASE QuizzingDB;
USE QuizzingDB;

DROP TABLE IF EXISTS Attempts; #tracks student attemps
DROP TABLE IF EXISTS Questions; #stores questions (input from professor)
DROP TABLE IF EXISTS LearningObjectives; #stores learning objectives (input from student or professor)
DROP TABLE IF EXISTS Students; #keeps track of students and accounts
DROP TABLE IF EXISTS Archives; #past information


CREATE TABLE UserAccounts (
    userId int auto_increment PRIMARY KEY,              
    -- googleId VARCHAR(255) unique not null, #verify what this will look like 
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,  #merrimack email
    isInstructor BOOLEAN DEFAULT FALSE,
    firstName varchar(20),
	lastName varchar(50)
);

CREATE TABLE Students (
studentId int primary key,
firstName varchar(20),
lastName varchar(50),
badge VARCHAR(100), #i will eventually make a bank of badges
totalPoints int,
major varchar(100),
email VARCHAR(150) UNIQUE NOT NULL,
foreign key (studentId) references UserAccounts(userId) on delete cascade
);

CREATE TABLE Instructors (
instructorId int primary key,
schoolSubject varchar(50),
firstName varchar(20),
lastName varchar(50),
email VARCHAR(150) UNIQUE NOT NULL,
foreign key (instructorId) references UserAccounts(userId) on delete cascade
);

CREATE TABLE Classroom (
classId int primary key,
className varchar(100) not null,
firstName varchar(20),
lastName varchar(50),
instructorId int not null,
foreign key (instructorId) references UserAccounts(userId) on delete cascade
);

CREATE TABLE ClassEnrollees (
classId int not null,
studentId int not null,
primary key (classId, studentId),
foreign key (classId) references Classroom(classId) on delete cascade,
foreign key (studentId) references Students(studentId) on delete cascade
);

CREATE TABLE Quizzes (
    quizId int primary key auto_increment,
    quizName varchar(100),
    instructorId INT, #instructor who created it
    classId int not null,
    foreign key (instructorId) references Instructors(instructorId) on delete cascade,
    foreign key (classId) references Classroom(classId) on delete cascade
);

CREATE TABLE Questions (
    questionId INT PRIMARY KEY AUTO_INCREMENT,
    questionNumber int,
    questionText TEXT NOT NULL,
    quizId INT NOT NULL,
    difficulty ENUM('easy','medium','hard') NOT NULL,
    correct_choice_id INT,
    FOREIGN KEY (quizId) REFERENCES Quizzes(quizId) ON DELETE CASCADE
);

CREATE TABLE QuestionChoices (
    choiceId INT PRIMARY KEY AUTO_INCREMENT,
    questionId INT NOT NULL,
    choiceLabel CHAR(1),
    choiceText VARCHAR(300) NOT NULL,
    FOREIGN KEY (questionId) REFERENCES Questions(questionId) ON DELETE CASCADE
);

-- add the foreign key after both tables exist (to get rid of circular dependency)
ALTER TABLE Questions
ADD CONSTRAINT fk_correct_choice
FOREIGN KEY (correct_choice_id) REFERENCES QuestionChoices(choiceId);


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

CREATE TABLE Badges (
  badgeId INT AUTO_INCREMENT PRIMARY KEY,
  badgeName VARCHAR(100) UNIQUE NOT NULL,
  description TEXT,
  pointThreshold INT
);

CREATE TABLE StudentBadges (
    studentId INT NOT NULL,
    badgeId INT NOT NULL,
    PRIMARY KEY (studentId, badgeId),
    FOREIGN KEY (studentId) REFERENCES Students(studentId) ON DELETE CASCADE,
    FOREIGN KEY (badgeId) REFERENCES Badges(badgeId) ON DELETE CASCADE
);

CREATE TABLE Readings (
    readingId INT AUTO_INCREMENT PRIMARY KEY,
    instructorId INT NOT NULL,
    classId INT,
    readingName VARCHAR(255) NOT NULL,
    filePath VARCHAR(500), 
    uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructorId) REFERENCES Instructors(instructorId) ON DELETE CASCADE,
    FOREIGN KEY (classId) REFERENCES Classroom(classId) ON DELETE SET NULL
);

CREATE TABLE ReadingObjectives (
    objectiveId INT AUTO_INCREMENT PRIMARY KEY,
    readingId INT NOT NULL,
    classId int not null,
    objectiveName VARCHAR(255) NOT NULL,
    -- confidenceScore DECIMAL(5,2),-- commented out for now...this is likely necessary as I implement machine learning s 
    isApproved BOOLEAN DEFAULT FALSE, 
    FOREIGN KEY (readingId) REFERENCES Readings(readingId) ON DELETE CASCADE,
    FOREIGN KEY (classId) REFERENCES Classroom(classId) ON DELETE CASCADE
);

CREATE TABLE QuestionObjectives (
    questionId INT,
    objectiveId INT,
    PRIMARY KEY (questionId, objectiveId),
    FOREIGN KEY (questionId) REFERENCES Questions(questionId) ON DELETE CASCADE,
    FOREIGN KEY (objectiveId) REFERENCES readingObjectives(objectiveId) ON DELETE CASCADE
);

/*Makes sure that unneccessary learning objectives aren't displayed for a student!!!*/
SELECT DISTINCT lo.*
FROM readingObjectives lo
JOIN QuestionObjectives qo ON lo.objectiveId = qo.objectiveId;