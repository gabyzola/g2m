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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import jakarta.annotation.PostConstruct;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;

@Repository
public class QuizDal {

    //creates connection object
    private Connection myConnection;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    //do not put any args here!
    public QuizDal() {}

    @PostConstruct
    public void init() {
        myConnection = getMySQLConnection(dbUrl, user, password);
    }

    //getMySQLConnection
    //establishes connection to the database-- this is a local instance right now!
    private Connection getMySQLConnection(String url, String user, String password) {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, password);
        System.out.println("Connection successful! Connected to: " + url);
        return con;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException exception) {
            System.out.println("Failed to connect to the database: " + exception.getMessage());
        }
        return null;
    }

    //register new user
    //bl: done
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

    //create class- instructor does this!
    //bl: done
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

    //display instructor's classes
    //bl:done
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

    //enroll student in a class- owner of the class will only have this privilege
    //bl: done
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

    //lists class enrollees
    //bl:done
    public List<Map<String, Object>> searchForEnrolleesByClass(int classId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{Call getEnrolleesByClass(?)}")) {
            
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

    //lists student's classes
    //bl: done
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

    //insert reading into class module- another instructor only priv
    //bl: done
    public boolean insertNewReading(int instructorId, int classId, String readingName, String filePath) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewReading(?, ?, ?, ?)}");
            stmt.setInt(1, instructorId);
            stmt.setInt(2, classId);
            stmt.setString(3, readingName);
            stmt.setString(4, filePath);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding reading.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //insert reading objectives into proper table (see business layer)
    public boolean insertNewReadingObjective(int readingId, int classId, String objectiveName) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewReadingObjective(?, ?, ?)}");
            stmt.setInt(1, readingId);
            stmt.setInt(2, classId);
            stmt.setString(3, objectiveName);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding objective.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //create quiz- instructor priv
    //creates a quiz module- sets quiz name and automatically passes classId and instructorId
    public int insertNewQuiz(String quizName, int instructorId, int classId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewQuiz(?, ?, ?, ?)}");
            stmt.setString(1, quizName);
            stmt.setInt(2, instructorId);
            stmt.setInt(3, classId);
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);

            stmt.execute();

            int newQuizId = stmt.getInt(4); // retrieve the OUT parameter
            System.out.println("New quiz inserted with ID: " + newQuizId);
            return newQuizId;

        } catch (SQLException e) {
            System.out.println("Error inserting new quiz.");
            e.printStackTrace();
            return -1;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    //assign reading to quiz
    //during quiz creation there will be a button where the prof can link a reading
    //when they link a reading, the relevant objectives will populate
    public boolean insertQuizReading(int quizId, int readingId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertQuizReading(?, ?)}");
            stmt.setInt(1, quizId);
            stmt.setInt(2, readingId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error linking quiz and reading.");
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    //insert new question + their objectives
    //actually inserts the question, its choices, its assigned objective, and marks the correct choice
    public boolean insertNewQuestion(QuestionData qData, int questionNumber) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewQuestion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, questionNumber);                 
            stmt.setString(2, qData.getQuestionText());     
            stmt.setString(3, qData.getDifficulty());       
            stmt.setString(4, qData.getChoiceA());          
            stmt.setString(5, qData.getChoiceB());           
            stmt.setString(6, qData.getChoiceC());         
            stmt.setString(7, qData.getChoiceD());        
            stmt.setString(8, String.valueOf(qData.getCorrectAnswer())); 
            stmt.setInt(9, qData.getObjectiveId());         
            stmt.setInt(10, qData.getQuizId());             

            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting new question.");
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNextQuestionNumberForQuiz(int quizId) throws SQLException {
        String sql = "SELECT IFNULL(MAX(questionNumber), 0) + 1 AS nextNum FROM Questions WHERE quizId = ?";
        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("nextNum");
            } else {
                return 1;
            }
        }
    }

    //display quizzes within a class
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

    //display relevant quiz objectives to choose from
    public List<Map<String, Object>> getObjectivesByQuiz(int quizId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getQuizObjectives(?)}")) {
            cs.setInt(1, quizId);
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

    //display ALL quiz questions
    // Fetches all quiz questions as raw data without wrapping into QuizQuestion
    public List<Map<String, Object>> getQuizQuestions(int quizId) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (CallableStatement cs = myConnection.prepareCall("{CALL getQuizQuestions(?)}")) {
            cs.setInt(1, quizId);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("questionId", rs.getInt("questionId"));
                    row.put("questionNumber", rs.getInt("questionNumber")); // NOT questionId
                    row.put("questionText", rs.getString("questionText"));
                    row.put("difficulty", rs.getString("difficulty").toUpperCase());
                    row.put("learningObjective", rs.getString("learningObjective"));
                    row.put("choiceId", rs.getInt("choiceId"));
                    row.put("choiceLabel", rs.getString("choiceLabel"));
                    row.put("choiceText", rs.getString("choiceText"));
                    row.put("correctChoiceId", rs.getInt("correct_choice_id"));

                    results.add(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }


    //student selects objective before taking the quiz
    public boolean chooseLearningObjective(int studentId, int quizId, int objectiveId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL SelectStudentObjective(?, ?, ?)}");
            stmt.setInt(1, studentId);
            stmt.setInt(2, objectiveId);
            stmt.setInt(3, objectiveId);

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

    //get student objectives
    public List<Integer> getStudentObjective(int studentId) {
        List<Integer> results = new ArrayList<>();

        try (CallableStatement cs = myConnection.prepareCall("{CALL getStudentObjectives(?)}")) {
            cs.setInt(1, studentId);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getInt("objectiveId")); 
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching student objectives.");
            e.printStackTrace();
        }
        return results;
    }

    //get next question (or first question)
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

    //submit answer-- student gets feedback after each question
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

    //get quiz score after last question is answered
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

    //assign badge
    public boolean assignBadge(int studentId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL assignBadge(?)}");
            stmt.setInt(1, studentId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error assigning badge.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //display all the students badges
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

    //display all badges that can be earned
    public ArrayList<Badge> getAllBadges() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Badges");
            ArrayList<Badge> Badges = new ArrayList<>();

            while (myRelation.next()) {
                Badge newBadge = new Badge(myRelation.getInt("badgeId"), myRelation.getString("badgeName"), myRelation.getInt("pointThreshold"));
                Badges.add(newBadge);

            }

            return Badges;

        } catch (SQLException e) { 
            e.printStackTrace();
            return null;
        }
    }

    
    /*DELETIONS */
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

    
    /*misc. testing procs */
    //getAllStudents

    public ArrayList<Student> getAllStudents() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Students");

            ArrayList<Student> Students = new ArrayList<>();

            while (myRelation.next()) {
                Student newStudent = new Student(myRelation.getInt("StudentId"), myRelation.getString("badge"),
                        myRelation.getInt("totalPoints"), myRelation.getString("major"));
                Students.add(newStudent);

            }

            return Students; 

        } catch (SQLException e) { 
            e.printStackTrace();
            return null;
        }
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

    // get the most recent quiz created by this instructor
    public int getLastQuizId(int instructorId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = myConnection.prepareStatement(
                "SELECT quizId FROM Quizzes WHERE instructorId = ? ORDER BY quizId DESC LIMIT 1"
            );
            stmt.setInt(1, instructorId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quizId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return -1; // failed
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

    
           



    
           

