DROP TABLE IF EXISTS Attempts; #tracks student attemps
DROP TABLE IF EXISTS Questions; #stores questions (input from professor)
DROP TABLE IF EXISTS LearningObjectives; #stores learning objectives (input from student or professor)
DROP TABLE IF EXISTS Students; #keeps track of students and accounts
DROP TABLE IF EXISTS Archives; #past information

CREATE TABLE Students (
    student_id SERIAL PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE
);

CREATE TABLE LearningObjectives (
    objective_id SERIAL PRIMARY KEY,
    objective_name VARCHAR(200) NOT NULL,
    description TEXT
);

CREATE TABLE Questions (
    question_id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    choice_a VARCHAR(300) NOT NULL,
    choice_b VARCHAR(300) NOT NULL,
    choice_c VARCHAR(300) NOT NULL,
    choice_d VARCHAR(300) NOT NULL,
    correct_answer CHAR(1) NOT NULL CHECK (correct_answer IN ('A','B','C','D')), #check with prof: do they always want it to be 4 options? or should this be flexible
    difficulty VARCHAR(10) NOT NULL CHECK (difficulty IN ('easy','medium','hard')),
    objective_id INT REFERENCES LearningObjectives(objective_id)
);

CREATE TABLE Attempts (
    attempt_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES Students(student_id),
    question_id INT REFERENCES Questions(question_id),
    chosen_answer CHAR(1) CHECK (chosen_answer IN ('A','B','C','D')), #check with prof: do they always want it to be 4 options? or should this be flexible
    is_correct BOOLEAN,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/*
no clue how to set this up rn, deal with it later
CREATE TABLE Archives (

);
*/