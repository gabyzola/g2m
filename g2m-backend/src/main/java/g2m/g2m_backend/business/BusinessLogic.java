package g2m.g2m_backend.business;

//import g2m.DAL.QuizDal;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.QuizDal;



import java.util.*;

public class BusinessLogic {

    private QuizDal dal;

    // store cached data
    public ArrayList<Student> students = new ArrayList<>();
    public ArrayList<Badge> badges = new ArrayList<>();

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
        this.students = dal.getAllStudents();
        this.badges = dal.getAllBadges();
    }

    /*STUDENT MANAGEMENT*/

    //register a new user 
    public boolean registerUser(String username, String email, boolean isInstructor,
                                String major, String schoolSubject, String firstName, String lastName) {
        return dal.insertNewUser(username, email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    //create new class
    public boolean createClass(int classId, String className, String instructorEmail) {
        return dal.insertNewClass(classId, className, instructorEmail);
    }

    //display instructor classes
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

    //enroll a student in a class by email
    public boolean enrollStudentInClass(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    //list class enrollees
    //view all class enrollees for a class
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

    //display student classes
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

    //add reading to a class module

    //get learning objectives from reading and store it in the db

    //create quiz + add questions
    public boolean createQuiz(String quizName, int instructorId, int classId,
                              List<QuestionData> questions) {
        try {
            //add in basic info
            boolean quizCreated = dal.insertNewQuiz(quizName, instructorId, classId);
            if (!quizCreated) {
                System.out.println("Failed to create quiz.");
                return false;
            }
            System.out.println("Quiz created successfully.");

            // add each question to seperate class for storage
            for (QuestionData q : questions) {
                boolean questionAdded = dal.insertNewQuestion(
                        q.getQuestionText(),
                        q.getDifficulty(),
                        q.getChoiceA(),
                        q.getChoiceB(),
                        q.getChoiceC(),
                        q.getChoiceD(),
                        q.getCorrectAnswer(),
                        q.getObjectiveId(),
                        q.getQuizId() 
                );

                if (questionAdded) {
                    System.out.println("Question added: " + q.getQuestionText());
                } else {
                    System.out.println("Failed to add question: " + q.getQuestionText());
                }
            }

            //placeholder for testing
            System.out.println("Quiz published successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //display quizzes
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

    //display relavant learning objectives based on the quiz

    //take quiz + select learning objective + get questions + get score (+ maybe get badge!)

    //display student's badges
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

    //display all badges available to earn
    public void displayAllBadges() {
        System.out.println("All Available Badges:");
        for (Badge badge : badges) {
            System.out.println("- " + badge.getBadgeName() + " (Requires " + badge.getPointThreshold() + " pts)");
        }
    }
    


    //take a quiz
    public void takeQuiz(int studentId, int quizId, Scanner in) {
        System.out.println("Starting quiz...");

        boolean moreQuestions = true;

        while (moreQuestions) {
            //get the next question (will soon be machine learning)
            Map<String, Object> questionData = dal.getNextQuestion(quizId, studentId);

            if (questionData == null || questionData.isEmpty()) {
                System.out.println("No more questions available. Submitting quiz...");
                moreQuestions = false;
                break;
            }

            //get question text and id (maybe not id we'll see)
            String questionText = (String) questionData.get("questionText");
            int questionId = (int) questionData.get("questionId");

            System.out.println("\nQuestion: " + questionText);

            //create a map to link labels (A/B/C/D) to choiceIds
            Map<String, Integer> choiceMap = new HashMap<>();

            //loop through possible choices (A–D)
            for (char label = 'A'; label <= 'D'; label++) {
                String textKey = "choice" + label + "_Text";
                String idKey = "choice" + label + "_Id";

                if (questionData.containsKey(textKey) && questionData.get(textKey) != null) {
                    System.out.println(label + ". " + questionData.get(textKey));
                    choiceMap.put(String.valueOf(label), (int) questionData.get(idKey));
                }
            }

            System.out.print("Enter your answer (A/B/C/D): ");
            String answer = in.nextLine().trim().toUpperCase();

            //check student's input
            if (!choiceMap.containsKey(answer)) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            int chosenChoiceId = choiceMap.get(answer);

            //submit the answer
            Map<String, Object> response = dal.submitAnswer(
                    studentId,
                    quizId,
                    questionId,
                    chosenChoiceId
            );

            //handle the response
            if (response != null && response.containsKey("isCorrect")) {
                boolean isCorrect;

                // MySQL returns tinyint(1) as Integer sometimes, so convert safely
                Object rawValue = response.get("isCorrect");
                if (rawValue instanceof Boolean) {
                    isCorrect = (Boolean) rawValue;
                } else if (rawValue instanceof Number) {
                    isCorrect = ((Number) rawValue).intValue() == 1;
                } else {
                    isCorrect = Boolean.parseBoolean(rawValue.toString());
                }

                System.out.println(isCorrect ? "Correct!" : "Incorrect.");
            }
        }

        //after quiz ends, show score
        Map<String, Object> score = dal.getQuizScore(studentId, quizId);
        if (!score.isEmpty()) {
            System.out.println("\n=== Quiz Completed ===");
            System.out.println("Score: " + score.get("totalScore") + "/" + score.get("totalQuestions"));
            System.out.println("Percentage: " + score.get("percentage") + "%");
        }
    }



    /*misc*/

    //search for a student by email, name, or ID
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

    public static void printStudents(ArrayList<Student> students) {
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public void closeConnection() {
        dal.close();
    }
}
