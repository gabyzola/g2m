package g2m.g2m_backend.DAL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.User;
import jakarta.annotation.PostConstruct;

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
    //establishes connection to the database-- springboot automatically injects these from application.properties
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
    public boolean insertNewUser(
            String googleSub,
            String email,
            boolean isInstructor,
            String major,
            String schoolSubject,
            String firstName,
            String lastName)
    {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL InsertNewUser(?, ?, ?, ?, ?, ?, ?)}");

            stmt.setString(1, googleSub);
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
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //gets user id based on google sub, i do this when anew page loads EVERY TIME or else people's database info will NOT show up
    //bl: done
    public Map<String, Object> lookupUserIdBySub(String googleSub) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL LookupUserBySub(?)}");
            stmt.setString(1, googleSub);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("userId"); 
                String email = rs.getString("email"); 

                if (rs.wasNull() || userId == 0 || email == null) {
                    return Map.of("userId", -1, "email", null);
                }

                return Map.of("userId", userId, "email", email);
            }
            return Map.of("userId", -1, "email", null);

        } catch (SQLException e) {
            System.out.println("Error looking up user: " + e.getMessage());
            return Map.of("userId", -1, "email", null);

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //just returns an int-- this was from the original log in method. keeping this for futuure use
    //bl: done
    public int lookupUserIdByEmail(String email) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL LookupUser(?)}");
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("userId"); 
                if (rs.wasNull()) return -1;  // user not found
                return id;
            }
            return -1; 


        } catch (SQLException e) {
            System.out.println("Error looking up user: " + e.getMessage());
            return -1;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //determines student or instructor to get the right privs
    //bl:done
    public int getUserRole(int userId) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL isInstructorCheck(?)}");
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int role = rs.getInt("isInstructor"); // 1 for instructor, 0 for student
                if (rs.wasNull()) return -1;          // user not found
                return role;
            }

            return -1; // no rows returned, user not found

        } catch (SQLException e) {
            System.out.println("Error fetching user role: " + e.getMessage());
            return -1;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //finds a user object based on the google sub
    //bl: not used there, used in this file!
    public User findUserBySub(String googleSub) throws SQLException {
        String sql = "SELECT * FROM UserAccounts WHERE googleSub = ?";
        PreparedStatement stmt = myConnection.prepareStatement(sql);
        stmt.setString(1, googleSub);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                rs.getString("googleSub"),
                rs.getInt("userId"),
                rs.getString("email"),
                rs.getBoolean("isInstructor"),
                rs.getString("firstName"),
                rs.getString("lastName")
            );
        }
        return null;
    }

    //registration process to get an existing user by sub or create one with new sub
    //bl:done
    public User getOrCreateUser(String googleSub, String email, boolean isInstructor,
                            String major, String schoolSubject,
                            String firstName, String lastName) 
        {
            try {
            User existing = findUserBySub(googleSub);
            if (existing != null) return existing;

            insertNewUser(googleSub, email, isInstructor, major, schoolSubject, firstName, lastName);
            return findUserBySub(googleSub);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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

    //lists class enrollees (in the sidear in UI OR in manage enrollees file)
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
                row.put("email", rs.getString("email"));
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
    public int insertNewReading(int instructorId, int classId, String readingName) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewReading(?, ?, ?)}");
            stmt.setInt(1, instructorId);
            stmt.setInt(2, classId);
            stmt.setString(3, readingName);

            boolean hasResults = stmt.execute();

            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    return rs.getInt("readingId");  
                }
            }
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
    }

    //puts a reading objective under a certain reading. this is a must for the adaptive quizzing!
    //bl:done
    public int insertNewReadingObjective(int readingId, int classId, String objectiveName) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL InsertNewReadingObjective(?, ?, ?)}");
            stmt.setInt(1, readingId);
            stmt.setInt(2, classId);
            stmt.setString(3, objectiveName);

            boolean hasResults = stmt.execute();
            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    return rs.getInt("objectiveId");   // ← returned from SELECT LAST_INSERT_ID()
                }
            }

            return -1;  

        } catch (SQLException e) {
            System.out.println("Error adding objective.");
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
        }
    }

    //get all the readings in a class (for creating quiz OR in classroom sidebar)
    //bl: done
    public List<Map<String, Object>> getClassReadings(int classId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getReadingsByClass(?)}")) {
            cs.setInt(1, classId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("readingId", rs.getInt("readingId"));
                row.put("readingName", rs.getString("readingName"));
                row.put("filePath", rs.getString("filePath"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //just sticks new quiz in db-- all the info will get added later
    //bl: done
    public int insertQuiz(int instructorId, int classId) {
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = myConnection.prepareCall("{CALL InsertQuiz(?, ?)}");
            stmt.setInt(1, instructorId);
            stmt.setInt(2, classId);
            boolean hasResult = stmt.execute();

            if (hasResult) {
                rs = stmt.getResultSet();
                if (rs.next()) {
                    int newQuizId = rs.getInt("quizId");
                    System.out.println("New quiz inserted with ID: " + newQuizId);
                    return newQuizId;
                }
            }
            System.out.println("No quiz ID returned from procedure.");
            return -1;

        } catch (SQLException e) {
            System.out.println("Error inserting new quiz.");
            e.printStackTrace();
            return -1;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //assign reading to quiz, during quiz creation there will be a button where the prof can link a reading, when they link a reading, the relevant objectives will populate
    //bl: done
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

    //insert new question + their objectives actually inserts the question, its choices, its assigned objective, and marks the correct choice
    //bl: done
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

    //keeps track of question numbers
    //bl: done
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
    //bl: done
    public List<Map<String, Object>> getQuizzesByClass(int classId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getQuizzesByClass(?)}")) {
            cs.setInt(1, classId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("quizId", rs.getInt("quizId"));
                row.put("quizName", rs.getString("quizName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //display relevant quiz objectives to choose from
    //bl: done
    public List<Map<String, Object>> getObjectivesByQuiz(int quizId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getQuizObjectives(?)}")) {
            cs.setInt(1, quizId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("objectiveId", rs.getInt("objectiveId"));
                row.put("objectiveName", rs.getString("objectiveName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //get objectives by reading, when an instrucxtor chooses a reading for the quiz to be based on
    //bl: done
    public List<Map<String, Object>> getReadingObjectives(int readingId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = myConnection.prepareCall("{CALL getReadingObjectives(?)}")) {
            cs.setInt(1, readingId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("objectiveId", rs.getInt("objectiveId"));
                row.put("objectiveName", rs.getString("objectiveName"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    //display ALL quiz questions, regardless of chosen objectives
    //bl: done
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
                    row.put("objectiveId", rs.getInt("objectiveId"));
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

    //student selects objective before taking the quiz, they must do this if they want an adaptive quiz!
    //bl: done
    public boolean chooseLearningObjective(int studentId, int quizId, int objectiveId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL SelectStudentObjective(?, ?, ?)}");
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);
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
 
    //get student objectives idk if this is actually needed in the ui, but i already coded it
    //bl: done
   public List<Map<String, Object>> getStudentObjectives(int studentId) {
    List<Map<String, Object>> results = new ArrayList<>();

    try (CallableStatement cs = myConnection.prepareCall("{CALL getStudentObjectives(?)}")) {
        cs.setInt(1, studentId);

        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("objectiveId", rs.getInt("objectiveId"));
                row.put("objectiveName", rs.getString("objectiveName"));
                results.add(row);
            }
        }

        } catch (SQLException e) {
            System.out.println("Error fetching student objectives.");
            e.printStackTrace();
        }

        return results;
    }

    //submit answer-- student gets feedback after each question
    //bl:
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
    //bl:
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
    //bl:
    public String assignBadge(int studentId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL assignBadge(?)}");
            stmt.setInt(1, studentId);

            boolean hasResult = stmt.execute();

            if (hasResult) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    return rs.getString("newBadge"); // may be null
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

 
    //display all the students badges
    //bl: done
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
    //bl:done
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

    //returns class name for ui population
    //bl:done
    public String getClassName(int classId) {
        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            ResultSet rs = myStatement.executeQuery(
                "SELECT className FROM Classroom WHERE classId = " + classId
            );

            if (rs.next()) {
                return rs.getString("className");
            } else {
                return null; 
            }

        } catch (SQLException e) { 
            e.printStackTrace();
            return null;
        }
    }

    //takes existing quiz from insert quiz and updates th4e name
    //bl:done
    public boolean updateQuizName(int quizId, String quizName) {
        String sql = "{CALL updateQuizName(?, ?)}";
        try (CallableStatement stmt = myConnection.prepareCall(sql)) {

            stmt.setInt(1, quizId);
            stmt.setString(2, quizName);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //starts attempt session right after a student picks their objectives
    public int startAttemptSession(int studentId, int quizId, Integer objectiveId) {
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = myConnection.prepareCall("{CALL StartAttemptSession(?, ?, ?)}");
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);

            if (objectiveId == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, objectiveId);
            }

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("sessionId");
            }

            return -1;

        } catch (SQLException e) {
            System.out.println("Error starting attempt session: " + e.getMessage());
            return -1;

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignore) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }


    //saves each student answer
    public boolean saveStudentAnswer(int sessionId, int questionId, int chosenChoiceId) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL SaveStudentAnswer(?, ?, ?)}");
            stmt.setInt(1, sessionId);
            stmt.setInt(2, questionId);
            stmt.setInt(3, chosenChoiceId);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving student answer: " + e.getMessage());
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //ends attempt session and finalizes the score
    public boolean finalizeAttemptSession(int sessionId) {
        CallableStatement stmt = null;

        try {
            stmt = myConnection.prepareCall("{CALL FinalizeAttemptSession(?)}");
            stmt.setInt(1, sessionId);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("Error finalizing attempt session: " + e.getMessage());
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }

    //gets full results (this is gonna return a LOT)
    public Map<String, Object> getSessionResults(int sessionId) {
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            stmt = myConnection.prepareCall("{CALL GetSessionResults(?)}");
            stmt.setInt(1, sessionId);

            boolean hasResults = stmt.execute();

            // First result set: AttemptSessions row
            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    Map<String, Object> session = new HashMap<>();
                    session.put("sessionId", rs.getInt("sessionId"));
                    session.put("studentId", rs.getInt("studentId"));
                    session.put("quizId", rs.getInt("quizId"));
                    session.put("objectiveId", rs.getInt("objectiveId"));
                    session.put("score", rs.getInt("score"));
                    session.put("percentage", rs.getBigDecimal("percentage"));
                    result.put("session", session);
                }
            }

            // Move to second result set: AttemptAnswers
            if (stmt.getMoreResults()) {
                ResultSet rs2 = stmt.getResultSet();
                List<Map<String, Object>> answers = new ArrayList<>();

                while (rs2.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("questionId", rs2.getInt("questionId"));
                    row.put("chosenChoiceId", rs2.getInt("chosenChoiceId"));
                    row.put("chosenLetter", rs2.getString("chosenLetter"));
                    row.put("correctLetter", rs2.getString("correctLetter"));
                    row.put("isCorrect", rs2.getBoolean("isCorrect"));
                    row.put("pointsEarned", rs2.getInt("pointsEarned"));
                    row.put("questionText", rs2.getString("questionText"));

                    answers.add(row);
                }

                result.put("answers", answers);
            }

            return result;

        } catch (SQLException e) {
            System.out.println("Error getting session results: " + e.getMessage());
            return Map.of();

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignore) {}
        }
    }


    /*DELETIONS */
    public boolean deleteQuestion(int questionId) { return executeSimpleDelete("{CALL DeleteQuestion(?)}", questionId); }
    public boolean deleteQuiz(int quizId) { return executeSimpleDelete("{CALL DeleteQuiz(?)}", quizId); }
    public boolean deleteClass(int classId) { return executeSimpleDelete("{CALL DeleteClass(?)}", classId); }
    public boolean deleteUser(int userId) { return executeSimpleDelete("{CALL DeleteUser(?)}", userId); }
    public boolean deleteStudentObjective(int userId) { return executeSimpleDelete("{CALL DeleteStudentObjectives(?)}", userId); }
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

    //create quiz- instructor priv
    //creates a quiz module- sets quiz name and automatically passes classId and instructorId
    //bl: done
    public int insertNewQuiz(String quizName, int instructorId, int classId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewQuiz(?, ?, ?, ?)}");
            stmt.setString(1, quizName);
            stmt.setInt(2, instructorId);
            stmt.setInt(3, classId);
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);

            stmt.execute();

            int newQuizId = stmt.getInt(4);
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

    //check that someone can create a quiz
    public boolean canCreateQuiz(int userId, int classId) {
        boolean canCreate = false;

        String sql = "{CALL canCreateQuiz(?, ?)}";

        try (CallableStatement cs = myConnection.prepareCall(sql)) {
            cs.setInt(1, userId);
            cs.setInt(2, classId);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                int result = rs.getInt("canCreate"); 
                canCreate = (result == 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return canCreate;
    }

    // misc/ unused 
    //i actually dont think im gonna use this after all- but ill keep this just in case! might be helpful down the road
    public User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM UserAccounts WHERE email = ?";
        PreparedStatement stmt = myConnection.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                rs.getString("googleSub"),
                rs.getInt("userId"),
                rs.getString("email"),
                rs.getBoolean("isInstructor"),
                rs.getString("firstName"),
                rs.getString("lastName")
            );
        }
        return null;
    }

    public ArrayList<HashMap<String, Object>> searchForStudentByEmail(String emailQuery) {
        ArrayList<HashMap<String, Object>> students = new ArrayList<>();
        String sql = "SELECT s.badge, s.studentId, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "JOIN UserAccounts u ON s.studentId = u.userId " +
                    "WHERE u.email LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + emailQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("studentId"));
                row.put("badge", rs.getString("badge"));
                row.put("totalPoints", rs.getInt("totalPoints"));
                row.put("major", rs.getString("major"));
                row.put("email", rs.getString("email"));

                students.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public ArrayList<HashMap<String, Object>> searchForStudentByName(String nameQuery) {
        ArrayList<HashMap<String, Object>> students = new ArrayList<>();
        String sql = "SELECT s.badge, s.studentId, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "JOIN UserAccounts u ON s.studentId = u.userId " +
                    "WHERE u.username LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + nameQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("studentId"));
                row.put("badge", rs.getString("badge"));
                row.put("totalPoints", rs.getInt("totalPoints"));
                row.put("major", rs.getString("major"));
                row.put("email", rs.getString("email"));

                students.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public ArrayList<HashMap<String, Object>> searchForStudentById(String idQuery) {
        ArrayList<HashMap<String, Object>> students = new ArrayList<>();
        String sql = "SELECT s.badge, s.studentId, u.email, s.major, s.totalPoints " +
                    "FROM Students s " +
                    "JOIN UserAccounts u ON s.studentId = u.userId " +
                    "WHERE s.studentId LIKE ?";

        try (PreparedStatement ps = myConnection.prepareStatement(sql)) {
            ps.setString(1, "%" + idQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("studentId", rs.getInt("studentId"));
                row.put("badge", rs.getString("badge"));
                row.put("totalPoints", rs.getInt("totalPoints"));
                row.put("major", rs.getString("major"));
                row.put("email", rs.getString("email"));

                students.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Integer getLatestSessionId(int studentId) {
        Integer sessionId = null;

        try (CallableStatement cs = myConnection.prepareCall("{CALL GetLatestSessionId(?)}")) {
            cs.setInt(1, studentId);

            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                sessionId = rs.getInt("sessionId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessionId; 
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