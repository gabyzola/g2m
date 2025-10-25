package g2m.DAL;

//import g2m.DAL.QuizDal;
import g2m.DAL.javaSQLobjects.Student;
import g2m.DAL.javaSQLobjects.Badge;

import java.util.*;

public class BusinessLogic {

    private QuizDal dal;

    // store cached data
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Badge> badges = new ArrayList<>();

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
        this.students = dal.getAllStudents();
        this.badges = dal.getAllBadges();
    }

    /* ============================
       STUDENT MANAGEMENT
       ============================ */

    // Register a new user (student or instructor)
    public boolean registerUser(String username, String email, boolean isInstructor,
                                String major, String schoolSubject, String firstName, String lastName) {
        return dal.insertNewUser(username, email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    // Search for a student by email, name, or ID
    public ArrayList<Student> searchStudent(String filterType, String query) {
        switch (filterType.toLowerCase()) {
            case "email":
                return dal.searchForStudentByEmail(query);
            case "name":
                return dal.searchForStudentByName(query);
            case "id":
                return dal.searchForStudentById(query);
            default:
                System.out.println("Invalid search type. Use: email, name, or id.");
                return new ArrayList<>();
        }
    }

    // Enroll a student in a class by email
    public boolean enrollStudentInClass(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    // View all classes for a student
    public void viewStudentClasses(int studentId) {
        List<Map<String, Object>> classes = dal.getStudentsClasses(studentId);
        if (classes.isEmpty()) {
            System.out.println("No classes found for student ID: " + studentId);
            return;
        }
        System.out.println("\nClasses Enrolled:");
        for (Map<String, Object> c : classes) {
            System.out.println("- " + c.get("className") + " (Instructor: " +
                    c.get("instructorFirstName") + " " + c.get("instructorLastName") + ")");
        }
    }

    /* ============================
       INSTRUCTOR MANAGEMENT
       ============================ */

    public boolean createClass(int classId, String className, String instructorEmail) {
        return dal.insertNewClass(classId, className, instructorEmail);
    }

    public boolean createQuiz(String quizName, int instructorId, int classId) {
        return dal.insertNewQuiz(quizName, instructorId, classId);
    }

    public boolean addQuestionToQuiz(String questionText, String difficulty,
                                     String choiceA, String choiceB, String choiceC, String choiceD,
                                     char correctAnswer, int objectiveId, int quizId) {
        return dal.insertNewQuestion(questionText, difficulty, choiceA, choiceB, choiceC, choiceD,
                correctAnswer, objectiveId, quizId);
    }

    /* ============================
       QUIZ WORKFLOW
       ============================ */

    // Start a quiz session
    public void takeQuiz(int studentId, int quizId, Scanner in) {
        System.out.println("Starting quiz...");

        boolean moreQuestions = true;

        while (moreQuestions) {
            Map<String, Object> question = dal.getNextQuestion(quizId, studentId);

            if (question == null || question.isEmpty()) {
                System.out.println("No more questions available. Submitting quiz...");
                moreQuestions = false;
                break;
            }

            System.out.println("\nQuestion: " + question.get("questionText"));
            System.out.println("A. " + question.get("choiceText"));

            System.out.print("Enter your answer (A/B/C/D): ");
            String answer = in.nextLine().trim().toUpperCase();

            // Simulate answer checking
            Map<String, Object> response = dal.submitAnswer(
                    studentId,
                    quizId,
                    (int) question.get("questionId"),
                    (int) question.get("choiceId")
            );

            if (response != null && response.containsKey("isCorrect")) {
                boolean isCorrect = (boolean) response.get("isCorrect");
                System.out.println(isCorrect ? "✅ Correct!" : "❌ Incorrect.");
            }
        }

        Map<String, Object> score = dal.getQuizScore(studentId, quizId);
        if (!score.isEmpty()) {
            System.out.println("\n=== Quiz Completed ===");
            System.out.println("Score: " + score.get("totalScore") + "/" + score.get("totalQuestions"));
            System.out.println("Percentage: " + score.get("percentage") + "%");
        }
    }

    /* ============================
       BADGES & PROGRESS
       ============================ */

    public void displayStudentBadges(int studentId) {
        List<Map<String, Object>> badges = dal.getStudentBadges(studentId);
        if (badges.isEmpty()) {
            System.out.println("No badges earned yet!");
        } else {
            System.out.println("Badges earned:");
            for (Map<String, Object> b : badges) {
                System.out.println("- " + b.get("badgeName"));
            }
        }
    }

    public void displayAllBadges() {
        System.out.println("All Available Badges:");
        for (Badge badge : badges) {
            System.out.println("- " + badge.getBadgeName() + " (Requires " + badge.getPointThreshold() + " pts)");
        }
    }

    /* ============================
       UTILITY METHODS
       ============================ */

    public static void printStudents(ArrayList<Student> students) {
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public void closeConnection() {
        dal.close();
    }
}
