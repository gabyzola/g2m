package g2m.g2m_backend.business;
//import g2m.DAL.QuizDal;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.QuizDal;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BusinessLogic {

    private QuizDal dal;

    // store cached data
    // public ArrayList<Student> students = new ArrayList<>();
    // public ArrayList<Badge> badges = new ArrayList<>();

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
    }

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
    public List<Map<String, Object>> viewInstructorClasses(int instructorId) {
        return dal.getInstructorClasses(instructorId);
    }

    //enroll a student in a class by email
    public boolean enrollStudentInClass(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    //list class enrollees
    //view all class enrollees for a class
    public List<Map<String, Object>> viewClassEnrollees(int classId) {
        return dal.searchForEnrolleesByClass(classId);
    }

    //display student classes
    public List<Map<String, Object>> viewStudentClasses(int studentId) {
        return dal.getStudentsClasses(studentId);
    }

    //add reading to a class module + get learning objectives from reading and store it in the db
    public boolean uploadReading(int instructorId, int classId, String readingName, String filePath) {
        try {
            // verify that path is valid
            if (filePath == null || !filePath.isEmpty()) {
                System.out.println("Invalid reading file.");
                return false;
            }

            //String filePath = readingFile.getAbsolutePath();
            boolean readingInserted = dal.insertNewReading(instructorId, classId, readingName, filePath);

            if (!readingInserted) {
                System.out.println("Failed to insert new reading.");
                return false;
            }

            // machine learning not ready yet, this is a placeholder
            List<String> extractedObjectives = new ArrayList<>();
            extractedObjectives.add("Understand key concepts from the text");
            extractedObjectives.add("Analyze author’s argument and tone");
            extractedObjectives.add("Summarize main ideas and supporting details");

        
            for (String objective : extractedObjectives) {
                boolean objectiveInserted = dal.insertNewReadingObjective(0, classId, objective);
                if (!objectiveInserted) {
                    System.out.println("Failed to insert objective: " + objective);
                }
            }

            System.out.println("Reading and objectives uploaded successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("Error uploading reading.");
            e.printStackTrace();
            return false;
        }
    }


    //create quiz + add questions
    public boolean createQuiz(String quizName, int instructorId, int classId, List<Integer> readingIds,
                              List<QuestionData> questions) {
        try {
            //add in basic info
            int quizId = dal.insertNewQuiz(quizName, instructorId, classId);
            if (quizId == -1) {
                System.out.println("Failed to create quiz.");
                return false;
            }
            System.out.println("Quiz created successfully with ID: " + quizId);

            for (int readingId : readingIds) {
            boolean linked = dal.insertQuizReading(quizId, readingId);
                if (!linked) {
                    System.out.println("Failed to link reading " + readingId + " to quiz.");
                }
            }

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
    public List<Map<String, Object>> viewQuizzesByClass(int classId) {
        return dal.getQuizzesByClass(classId);
    }

    //display relavant learning objectives based on the quiz
    public List<Map<String, Object>> viewObjectivesByQuiz(int quizId) {
        return dal.getObjectivesByQuiz(quizId);
    }

    //take quiz + select learning objective + get questions + get score (+ maybe get badge!)
    public Map<String, Object> takeQuiz(int studentId, int quizId, List<Integer> answers) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Quiz-taking logic not yet implemented for web API.");
        return result;
    }

    //display student's badges
    public List<Map<String, Object>> displayStudentBadges(int studentId) {
        return dal.getStudentBadges(studentId);
    }

    //display all badges available to earn
    public ArrayList<Badge> displayAllBadges() {
        return dal.getAllBadges();
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

    //placeholder binary method for adjusting difficulty
    //might end up sticking this somewhere else
    //this will be roped into the quiz taking process like the machine learning will be, but really doesnt need to be in the machine learning integration
    private String adjustDifficulty(String currentDifficulty, double performance) {
        if (performance > 0.8) {
            switch (currentDifficulty) {
                case "easy":
                    return "medium";
                case "medium":
                    return "hard";
            }
        } else if (performance < 0.4) {
            switch (currentDifficulty) {
                case "hard":
                    return "medium";
                case "medium":
                    return "easy";
            }
        }
        return currentDifficulty;
    }

    public void closeConnection() {
        dal.close();
    }
}
