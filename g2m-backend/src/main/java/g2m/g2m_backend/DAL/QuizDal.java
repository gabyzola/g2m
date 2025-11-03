package g2m.g2m_backend.DAL;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;

public class QuizDal {

    //creates connection object
    private Connection myConnection;

    //establishes connection with name, username, and password
    public QuizDal(String databaseName, String user, String password) {
        myConnection = getMySQLConnection(databaseName, user, password);

    }

    //getMySQLConnection
    //establishes connection to the database-- this is a local instance!
    private Connection getMySQLConnection(String databaseName, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, user, password);
            System.out.println("Connection to " + databaseName + " database successful! Ready for use.");
            return con;
        }
         catch (ClassNotFoundException e) {
        System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException exception) {
            System.out.println("Failed to connect to the database" + exception.getMessage());
        }
        return null;
    }

    //GET METHODS

    //getAllStudents
    // Get all Students stored in the database returns as an arraylist
    public ArrayList<Student> getAllStudents() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            //get relations via a sql query
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Students");

            //create students arraylist
            ArrayList<Student> Students = new ArrayList<>();

            //add each relation into arraylist-- studentid, badge, totalPoints, major
            while (myRelation.next()) {
                Student newStudent = new Student(myRelation.getInt("StudentId"), myRelation.getString("badge"),
                        myRelation.getInt("totalPoints"), myRelation.getString("major"));
                Students.add(newStudent);

            }

            return Students; // return the array list of Students

        } catch (SQLException e) { //error handling
            e.printStackTrace();
            return null;
        }
    }

    //searchForEnrolleesByClass
    //Get all classEnrollees from specific class-- include student name (gets this from Students table)
    public List<Map<String, Object>> searchForEnrolleesByClass(int classId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{Call getEnrolleeByClass(?)}")) {
            
            cs.setInt(1, classId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("studentId"));
                row.put("firstName", rs.getString("firstName"));
                row.put("lastName", rs.getString("lastName"));
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Failed to call searchForEnrolleesByClass stored procedure.");
            e.printStackTrace();
            return null;
        }

        return results;
    }

    //searchForStudentByEmail
    //LOOK UP STUDENT BY EMAIL
    public ArrayList<Student> searchForStudentByEmail(String emailQuery) {
        ArrayList<Student> students = new ArrayList<>();
        String sql = "SELECT s.badge, s.studentId, u.username, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "JOIN UserAccounts u ON s.studentId = u.userId " +
                    "WHERE u.email LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + emailQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("studentId"),
                    rs.getString("badge"),
                    rs.getInt("totalPoints"),
                    rs.getString("major")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    //searchForStudentByName
    //LOOK UP STUDENT BY NAME
    public ArrayList<Student> searchForStudentByName(String nameQuery) {
        ArrayList<Student> students = new ArrayList<>();
        String sql = "SELECT s.studentId, u.username, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "JOIN UserAccounts u ON s.studentId = u.userId " +
                    "WHERE u.username LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + nameQuery + "%");
            ResultSet rs = ps.executeQuery();

        while (rs.next()) {
                Student student = new Student(
                    rs.getInt("studentId"),
                    rs.getString("badge"),
                    rs.getInt("totalPoints"),
                    rs.getString("major")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    //searchForStudentById
    //LOOK UP STUDENT BY ID
    public ArrayList<Student> searchForStudentById(String IdQuery) {
        ArrayList<Student> students = new ArrayList<>();
        String sql = "SELECT s.studentId, u.username, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "WHERE studentId LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + IdQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("studentId"),
                    rs.getString("badge"),
                    rs.getInt("totalPoints"),
                    rs.getString("major")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    //getAllBadges
    //This must be called in order to display badges in the UI
    public ArrayList<Badge> getAllBadges() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            //get relations via a sql query
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Badges");
            ArrayList<Badge> Badges = new ArrayList<>();

            //add each relation into arraylist-- studentid, badge, totalPoints, major
            while (myRelation.next()) {
                Badge newBadge = new Badge(myRelation.getInt("badgeId"), myRelation.getString("badgeName"), myRelation.getInt("pointThreshold"));
                Badges.add(newBadge);

            }

            return Badges; // return the array list of Students

        } catch (SQLException e) { 
            e.printStackTrace();
            return null;
        }
    }

    //getStudentClasses
    //gets all classes for a specific student --> this is for displaying classes student is enrolled in when student clicks "View Classes" in UI
    public List<Map<String, Object>> getStudentsClasses(int studentId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getStudentsClasses(?)}")) {
            cs.setInt(1, studentId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("classId", rs.getInt("classId"));
                row.put("className", rs.getString("className"));
                row.put("instructorFirstName", rs.getString("instructorFirstName"));
                row.put("instructorLastName", rs.getString("instructorLastName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //GetStudentBadges
    //for displaying badges earned by student on student dashboard
    public List<Map<String, Object>> getStudentBadges(int studentId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getStudentBadges(?)}")) {
            cs.setInt(1, studentId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("badgeName", rs.getString("badgeName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //getInstructorClasses
    //gets all classes for a specific instructor --> this is for displaying classes instructor is teaching when instructor clicks "View Classes" in UI
    public List<Map<String, Object>> getInstructorClasses(int instructorId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getInstructorClasses(?)}")) {
            cs.setInt(1, instructorId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("classId", rs.getInt("classId"));
                row.put("className", rs.getString("className"));
                row.put("instructorFirstName", rs.getString("instructorFirstName"));
                row.put("instructorLastName", rs.getString("instructorLastName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //getQuizzesByClass
    //gets all quizzes for a specific class --> for displaying quizzes when student/instructor clicks on a class in UI
    public List<Map<String, Object>> getQuizzesByClass(int classId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getQuizzesByClass(?)}")) {
            cs.setInt(1, classId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("quizName", rs.getString("quizName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //getNextQuestion
    //for quizzing process --> called everytime a student clicks "Next Question" in UI
    //VERY LIKELY THAT THIS WILL CHANGE DUE TO MACHINE LEARNINFG INTEGRATION
    public Map<String, Object> getNextQuestion(int quizId, int studentId) {
        Map<String, Object> result = new HashMap<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL GetNextQuestion(?, ?)}")) {
            cs.setInt(1, quizId);
            cs.setInt(2, studentId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                result.put("questionId", rs.getInt("questionId"));
                result.put("questionText", rs.getString("questionText"));
                result.put("choiceId", rs.getInt("choiceId"));
                result.put("choiceLabel", rs.getString("choiceLabel"));
                result.put("choiceText", rs.getString("choiceText"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    //getQuizScore
    // Get quiz score --> good for csv, this must be called when student clicks "Submit Quiz" to display their score in UI,also for analytics on studentDashboard
    public Map<String, Object> getQuizScore(int studentId, int quizId) {
        Map<String, Object> result = new HashMap<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL GetQuizScore(?, ?)}")) {
            cs.setInt(1, studentId);
            cs.setInt(2, quizId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                result.put("totalScore", rs.getInt("totalScore"));
                result.put("totalQuestions", rs.getInt("totalQuestions"));
                result.put("percentage", rs.getDouble("percentage"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    //getReadingObjectives
    //When a student clicks "Take Quiz" or "Start Quiz", this must be called to display learning objectives for that quiz/reading


    //INSERT METHODS

    //enrollStudent
    //Enroll student in class --> inserts into classEnrollee table
    public boolean enrollStudent(int classId, String email) {
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = myConnection.prepareCall("{CALL EnrollStudent(?, ?)}");
            stmt.setInt(1, classId);
            stmt.setString(2, email);

            boolean hadResultSet = stmt.execute();

            if (hadResultSet) {
                rs = stmt.getResultSet();
                if (rs.next()) {
                    String message = rs.getString("message");
                    System.out.println(message);
                    return false;
                }
            }
            return true;

        // LOOK INTO THIS !!!!
        } catch (SQLException e) {
            System.out.println("Error enrolling student.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //insertNewUser
    // Insert new user (login) --> make sure this also adds to student or instructor table!
    public boolean insertNewUser(String username, String email, boolean isInstructor, String major, String schoolSubject, String firstName, String lastName) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewUser(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setBoolean(3, isInstructor);
            stmt.setString(4, major);
            stmt.setString(5, schoolSubject);
            stmt.setString(6, firstName);
            stmt.setString(7, lastName);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting new user.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    // insertNewClass
    //For professors to create new class
    public boolean insertNewClass(int classId, String className, String instructorEmail) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewClass(?, ?, ?)}");
            stmt.setInt(1, classId);
            stmt.setString(2, className);
            stmt.setString(3, instructorEmail);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting new class.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //insertNewQuiz
    // Instructors can create quizzes--> this must call insertnewquestion
    public boolean insertNewQuiz(String quizName, int instructorId, int classId) {
        CallableStatement stmt = null;
        try {
            //InsertNewQuiz calls InsertNewQuestion inside of it
            stmt = myConnection.prepareCall("{CALL InsertNewQuiz(?, ?, ?)}");
            stmt.setString(1, quizName);
            stmt.setInt(2, instructorId);
            stmt.setInt(3, classId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting new quiz.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Insert new question with choices and objective
    public boolean insertNewQuestion(String questionText, String difficulty,
                                    String choiceA, String choiceB, String choiceC, String choiceD,
                                    char correctAnswer, int objectiveId, int quizId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewQuestion(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, questionText);
            stmt.setString(2, difficulty);
            stmt.setString(3, choiceA);
            stmt.setString(4, choiceB);
            stmt.setString(5, choiceC);
            stmt.setString(6, choiceD);
            stmt.setString(7, String.valueOf(correctAnswer));
            stmt.setInt(8, objectiveId);
            stmt.setInt(9, quizId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting new question.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //Student chooses learning objective before starting quiz-- from display of readingObjectives for the quiz
    //ADD TO BUSINESS LAYER!!!- studentObjective table MUST reset after quiz is completed
    public boolean chooseLearningObjective(int studentId, int objectiveId, String objectiveName) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL SelectStudentObjective(?, ?, ?)}");
            stmt.setInt(1, studentId);
            stmt.setInt(2, objectiveId);
            stmt.setString(3, objectiveName);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error choosing learning objective.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // submitAnswer
    //adds to TEMPORARY??? attempts table --> this needs to be expanded upon for analytics purposes
    public Map<String, Object> submitAnswer(int studentId, int quizId, int questionId, int choiceId) {
        Map<String, Object> result = new HashMap<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL SubmitAnswer(?, ?, ?, ?)}")) {
            cs.setInt(1, studentId);
            cs.setInt(2, quizId);
            cs.setInt(3, questionId);
            cs.setInt(4, choiceId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                result.put("isCorrect", rs.getBoolean("isCorrect"));
                result.put("correctChoiceId", rs.getInt("correctChoiceId"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    //submitQuiz
    //Called when there is no getNextQuestion --> student clicks "Submit Quiz", badge MAY be assigned here

    //insertNewQuestion
    //Instructors can insert questions while creating quiz. Called when they click "Create Quiz" and called if they click "Add Another Question"
    
    //assignNewBadge
    //Called if student points exceed badge threshold upon quiz submission

    //MODIFY EXISTING DATA IN DATABASE 

    //modify question

    //modify class

    //modify quiz

    //modify user info

    //DELETIONS

    public boolean deleteQuestion(int questionId) { return executeSimpleDelete("{CALL DeleteQuestion(?)}", questionId); }
    public boolean deleteQuiz(int quizId) { return executeSimpleDelete("{CALL DeleteQuiz(?)}", quizId); }
    public boolean deleteClass(int classId) { return executeSimpleDelete("{CALL DeleteClass(?)}", classId); }
    public boolean deleteUser(int userId) { return executeSimpleDelete("{CALL DeleteUser(?)}", userId); }
    public boolean deleteClassEnrollee(int classId, int studentId) {
        return executeDoubleDelete("{CALL DeleteClassEnrollee(?, ?)}", classId, studentId);
    }
    public boolean deleteChoice(int choiceId) { return executeSimpleDelete("{CALL DeleteChoice(?)}", choiceId); }

    private boolean executeSimpleDelete(String sql, int param) {
        try (CallableStatement stmt = myConnection.prepareCall(sql)) {
            stmt.setInt(1, param);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeDoubleDelete(String sql, int p1, int p2) {
        try (CallableStatement stmt = myConnection.prepareCall(sql)) {
            stmt.setInt(1, p1);
            stmt.setInt(2, p2);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (myConnection != null && !myConnection.isClosed()) {
                myConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

/* Methods im unsure about keeping-- may be useful later*/
/*
SELECT DISTINCT lo.*
FROM readingObjectives lo
JOIN QuestionObjectives qo ON lo.objectiveId = qo.objectiveId;
*/

    
           



    
           

