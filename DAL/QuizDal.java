package g2m.DAL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.Callable;

import g2m.DAL.javaSQLobjects.Student;
//import g2m.DAL.javaSQLobjects.classEnrollee;
import g2m.DAL.javaSQLobjects.Badge;

public class QuizDal {
    private Connection myConnection;

    public QuizDal(String databaseName, String user, String password) {
        myConnection = getMySQLConnection(databaseName, user, password);

    }

    //establishes connection to the database-- this is a local instance!
    private Connection getMySQLConnection(String databaseName, String user, String password) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, user, password);
            System.out.println("Connection to " + databaseName + " database successful! Ready for use.");
            return con;
        } catch (SQLException exception) {
            System.out.println("Failed to connect to the database" + exception.getMessage());
            return null;
        }
    }

    // Get all Students stored in the database returns as an arraylist
    public ArrayList<Student> getAllStudents() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            //get relations via a sql query
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Student");

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

    //LOOK UP STUDENT BY EMAIL
    public ArrayList<Student> searchForStudentByEmail(String emailQuery) {
    ArrayList<Student> students = new ArrayList<>();
    String sql = "SELECT s.studentId, u.username, u.email, s.major, s.totalPoints " +
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


    //Enroll student in class
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

    // Insert new user (login) --> make sure this also adds to student or instructor table!
    public boolean insertNewUser(String username, String email, boolean isInstructor, String major, String schoolSubject, String firstName, String lastName) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewUser(?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setBoolean(3, isInstructor);
            stmt.setString(4, major);
            stmt.setString(5, schoolSubject);
            stmt.setString(6, firstName);
            stmt.setString(7, lastName);

            // Major only for students
            if (!isInstructor) {
                stmt.setString(5, major);
                stmt.setNull(6, java.sql.Types.VARCHAR);
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setString(6, schoolSubject);
            }

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

    // Insert new class
    public boolean insertNewClass(int classId, String className, int instructorId) {
        CallableStatement stmt = null;
        try {
            stmt = myConnection.prepareCall("{CALL InsertNewClass(?, ?, ?)}");
            stmt.setInt(1, classId);
            stmt.setString(2, className);
            stmt.setInt(3, instructorId);

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

    // Insert new quiz--> this must call insertnewquestion
    public boolean insertNewQuiz(String quizName, int instructorId, int classId) {
        CallableStatement stmt = null;
        try {
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

    //Get All Badges
    // Get all Students stored in the database returns as an arraylist
    public ArrayList<Badge> getAllBadges() {

        Statement myStatement;
        try {
            myStatement = myConnection.createStatement();
            //get relations via a sql query
            ResultSet myRelation = myStatement.executeQuery("SELECT * FROM Badges");

            //create students arraylist
            ArrayList<Badge> Badges = new ArrayList<>();

            //add each relation into arraylist-- studentid, badge, totalPoints, major
            while (myRelation.next()) {
                Badge newBadge = new Badge(myRelation.getInt("badgeId"), myRelation.getString("badgeName"), myRelation.getInt("pointThreshold"));
                Badges.add(newBadge);

            }

            return Badges; // return the array list of Students

        } catch (SQLException e) { //error handling
            e.printStackTrace();
            return null;
        }
    }

    //GetNextQuestion

    //submitAnswer --> automatically calls GetNextQuestion

    //getsQuizScore

    //AssignBadge

    //submitQuiz --> adds missing info to attempts table, checks for badge eligability and assigns badge if elligable

    //delete question

    //delete quiz

    //delete class

    //delete user --> student/instructor on cascade

    //delete enrollee

    //modify question

    //modify class

    //modify quiz

    //modify user info


}


    
           

