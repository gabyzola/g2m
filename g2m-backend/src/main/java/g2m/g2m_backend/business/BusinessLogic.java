package g2m.g2m_backend.business;
//import g2m.DAL.QuizDal;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
//import g2m.g2m_backend.DAL.javaSQLobjects.Question;
//import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion;
import g2m.g2m_backend.DAL.QuizDal;
//import g2m.g2m_backend.business.*;
import org.springframework.stereotype.Service;

//machine learning imports
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.*;

@Service
public class BusinessLogic {

    private QuizDal dal;

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

        
            //calls insertnewreadingobjectives to make sure theyre stored in db
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

    //just link reading to quiz without returning their objectives
    public boolean addReadingToQuiz(int quizId, int readingId) {
        return dal.insertQuizReading(quizId, readingId);
    }

    //link a reading to a quiz return the corresponding objectives
    public List<Map<String, Object>> addReadingToQuizReturn(int quizId, int readingId) {
        boolean success = dal.insertQuizReading(quizId, readingId);

        if (success) {
            return null;
        }
        else {
            return dal.getObjectivesByQuiz(quizId);
        }
    }

    //adds a question to a quiz
    public boolean addQuestionToQuiz(QuestionData questionData) {
        try {
            // Example: get current max question number for the quiz
            int nextQuestionNumber = dal.getNextQuestionNumberForQuiz(questionData.getQuizId());

            // Insert question
            return dal.insertNewQuestion(questionData, nextQuestionNumber);

        } catch (Exception e) {
            System.out.println("Failed to add question to quiz.");
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

    //display quiz questions
    public List<QuizQuestion> getQuizQuestions(int quizId) {
        List<QuizQuestion> questions = dal.getQuizQuestions(quizId);

        if (questions == null || questions.isEmpty()) {
            return null;
        } else {
            System.out.println("Loaded " + questions.size() + " questions for quiz ID: " + quizId);
        }
        return questions;
    }

    //student chooses learning objectives
    public boolean chooseLearningObjectives(int studentId, int objectiveId, String objectiveName) {
        return dal.chooseLearningObjective(studentId, objectiveId, objectiveName);
    }

    //return student objectives (needs to get sent to quiz taking process so it can choose questions)
    //display relavant learning objectives based on the quiz
    public List<Map<String, Object>> getStudentObjectives(int quizId) {
        return dal.getObjectivesByQuiz(quizId);
    }

    //starts quiz by getting all of the questions that match getStudentObjectives
    public QuizManager startQuiz(int quizId) {
        List<QuizQuestion> questions = dal.getQuizQuestions(quizId);
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("No questions found for quiz " + quizId);
        }
        return new QuizManager(questions);
    }


    public Map<String, Object> takeQuiz(int studentId, int quizId, List<String> submittedAnswers) {
        Map<String, Object> result = new HashMap<>();

        //start the quiz
        QuizManager manager = startQuiz(quizId);
        List<Map<String, Object>> questionResults = new ArrayList<>();
        int correctCount = 0;

        //iterate over submitted answers
        for (String answer : submittedAnswers) {
            if (manager.isQuizOver()) break;

            // get next question using quizManger
            QuizQuestion question = manager.getNextQuestion(correctCount > 0); 
            if (question == null) break;

            //check correctness
            boolean isCorrect = question.getChoices().stream()
                    .filter(c -> c.getChoiceLabel().equalsIgnoreCase(answer))
                    .anyMatch(c -> c.getChoiceLabel().equalsIgnoreCase(answer) && isCorrectChoice(c, question));

            if (isCorrect) correctCount++;

            //stores each individual result
            Map<String, Object> qResult = new HashMap<>();
            qResult.put("question", question.getQuestionText());
            qResult.put("selectedAnswer", answer);
            qResult.put("correct", isCorrect);
            questionResults.add(qResult);
        }

        //calcs the score
        double score = (submittedAnswers.isEmpty() ? 0 : (double) correctCount / submittedAnswers.size());
        result.put("score", score);
        result.put("details", questionResults);
        result.put("message", "Quiz completed!");

        return result;
    }

    // helper to check if choice is correct (assuming you store correct choiceLabel somewhere)
    private boolean isCorrectChoice(QuizQuestion.Choice choice, QuizQuestion question) {
        return choice.getChoiceId() == question.getCorrectChoiceId();
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

    public void closeConnection() {
        dal.close();
    }
}
