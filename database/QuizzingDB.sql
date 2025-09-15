DROP DATABASE IF EXISTS QuizzingDB;
CREATE DATABASE QuizzingDB;

DROP TABLE IF EXISTS Attempts; #tracks student attemps
DROP TABLE IF EXISTS Questions; #stores questions (input from professor)
DROP TABLE IF EXISTS LearningObjectives; #stores learning objectives (input from student or professor)
DROP TABLE IF EXISTS Students; #keeps track of students and accounts
DROP TABLE IF EXISTS Archives; #past information

#For now we will store email, but depending on legal's answer we can use a token from google
CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_instructor BOOLEAN DEFAULT FALSE
);

CREATE TABLE LearningObjectives (
    objective_id INT PRIMARY KEY AUTO_INCREMENT,
    objective_name VARCHAR(200) NOT NULL,
    description TEXT
);

CREATE TABLE QuestionObjectives (
    question_id INT,
    objective_id INT,
    PRIMARY KEY (question_id, objective_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id),
    FOREIGN KEY (objective_id) REFERENCES LearningObjectives(objective_id)
);

CREATE TABLE Questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL,
    difficulty ENUM('easy','medium','hard') NOT NULL,
    correct_choice_id INT #tie in to the correct option
);

CREATE TABLE QuestionChoices (
    choice_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT,
    choice_label CHAR(1), #A, B, C, D
    choice_text VARCHAR(300) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id)
);

CREATE TABLE Quizzes (
    quiz_id SERIAL PRIMARY KEY,
    user_id INT, #instructor who created it
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Attempts (
    attempt_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES Users(user_id),
    quiz_id INT REFERENCES Quizzes(quiz_id),
    question_id INT REFERENCES Questions(question_id),
    chosen_choice_id INT REFERENCES QuestionChoices(choice_id),
    is_correct BOOLEAN,
    points_earned INT DEFAULT 0,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);