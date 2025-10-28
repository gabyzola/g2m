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

    //View instructors classes
    public void viewInstructorClasses(int instructorId) {
        List<Map<String, Object>> classes = dal.getInstructorClasses(instructorId);
        if (classes.isEmpty()) {
            System.out.println("No classes found for instructor ID: " + instructorId);
            return;
        }
        System.out.println("\nClasses Taught:");
        for (Map<String, Object> c : classes) {
            System.out.println("- " + c.get("className") + " (Class ID: " +
                    c.get("classId") + ")");
        }
    }

    //View all class enrollees for a class
    public void viewClassEnrollees(int classId) {
        List<Map<String, Object>> enrollees = dal.searchForEnrolleesByClass(classId);
        if (enrollees.isEmpty()) {
            System.out.println("No enrollees found for class ID: " + classId);
            return;
        }
        System.out.println("\nClass Enrollees:");
        for (Map<String, Object> e : enrollees) {
            System.out.println("- " + e.get("firstName") + " " + e.get("lastName") + " (Email: " +
                    e.get("email") + ")");
        }
    }

    public void viewQuizzesByClass(int classId) {
        List<Map<String, Object>> quizzes = dal.getQuizzesByClass(classId);
        if (quizzes.isEmpty()) {
            System.out.println("No quizzes found for class ID: " + classId);
            return;
        }
        System.out.println("\nQuizzes for Class ID " + classId + ":");
        for (Map<String, Object> q : quizzes) {
            System.out.println("- " + q.get("quizName") + " (Quiz ID: " +
                    q.get("quizId") + ")");
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

        //add section where student can choose learning objectives from relevant readings

        boolean moreQuestions = true;

        while (moreQuestions) {
            //machine learning here that compares student's chosen objectives to the available pot of questions
            //machine learning that also takes a students previous question answer (right or wrong) and adjusts the difficulty of the next question
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
                System.out.println(isCorrect ? "Correct!" : "Incorrect.");
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

    //add readings
    //when the instructor puts a reading in MackAdapt, the name and filepath will be saved in the db and associated with that classID which is also automatically passed in
    //machine learning in this method that scans the reading and generates learning objectives based on the content of the reading

    //getAllReadingsForClass

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
