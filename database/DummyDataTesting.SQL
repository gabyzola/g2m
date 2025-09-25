USE QuizzingDB;   

-- =========================
-- 1) UserAccounts
-- =========================
INSERT INTO UserAccounts (userId, googleId, username, email, isInstructor)
VALUES
(1, 'google-001', 'susan_smith', 'smiths@merrimack.edu', TRUE),
(2, 'google-002', 'jane_doe', 'doej@merrimack.edu', TRUE),
(3, 'google-003', 'john_johnson', 'johnsonj@merrimack.edu', TRUE),
(4, 'google-004', 'jack_student', 'studentj@merrimack.edu', FALSE),
(5, 'google-005', 'meghan_person', 'personm@merrimack.edu', FALSE),
(6, 'google-006', 'josh_guy', 'guyj@merrimack.edu', FALSE);

-- =========================
-- 2) Instructors
-- =========================
INSERT INTO Instructors (instructorId, schoolSubject)
VALUES
(1, 'Computer Science'),
(2, 'Mathematics'),
(3, 'History');

-- =========================
-- 3) Students
-- =========================
INSERT INTO Students (studentId, badge, totalPoints, major)
VALUES
(4, NULL, 0, "Mathematics"),
(5, NULL, 0, "History"),
(6, NULL, 0, "Computer Science");

-- =========================
-- 4) Classrooms
-- =========================
INSERT INTO Classroom (classId, className, instructorId)
VALUES
(101, 'Intro to Databases', 1),
(102, 'Discrete Mathematics', 2),
(103, 'World History', 3);

-- =========================
-- 5) ClassEnrollees
-- Jack + Meghan → Databases; Josh → Math
-- =========================
INSERT INTO ClassEnrollees (classId, studentId)
VALUES
(101, 6),  -- Josh in Databases
(103, 5),  -- Meghan in history
(102, 4);  -- Jack in Math

/*Select from Students*/
select * from Students;

/*Select from Classroom*/
select * from Classroom;

/*Select from ClassEnrollees*/
select * from ClassEnrollees;

/*Select from Instructors*/
select * from Instructors;

/*Select from UserAccounts*/
select * from UserAccounts;

/*TC 1: Insert New Class*/
CALL InsertNewClass(104, "Algebra", 3);

/*CHECK*/
select * from Classroom; -- success
select * from Classroom where instructorId = 3;

/*TC 1.1: Enroll Jack*/
CALL EnrollStudent(104, "studentj@merrimack.edu");

/*CHECK*/
select * from ClassEnrollees; -- success

/*TC 2: Create new student*/
CALL InsertNewUser("google_7", "ashley_burnett", "burnetta@merrimack.edu", FALSE, "Computer Science", NULL);

/*CHECK*/
select * from UserAccounts;
select * from Students;