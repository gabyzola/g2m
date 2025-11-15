package g2m.g2m_backend.business;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion;
import g2m.g2m_backend.DAL.QuizDal;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessLogic {

    final QuizDal dal;

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
    }

    //register a new user
    //api: done
    public boolean registerUser(String username, String email, boolean isInstructor,
                                String major, String schoolSubject, String firstName, String lastName) {
        return dal.insertNewUser(username, email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    //create new class
    //api: done
    public boolean createClass(int classId, String className, String instructorEmail) {
        return dal.insertNewClass(classId, className, instructorEmail);
    }

    //display instructor classes
    //api: done
    public List<Map<String, Object>> viewInstructorClasses(int instructorId) {
        return dal.getInstructorClasses(instructorId);
    }

    //enroll a student in a class by email
    //api: done
    public boolean enrollStudentInClass(int classId, String email) {
        return dal.enrollStudent(classId, email);
    }

    //list class enrollees
    //api: done
    public List<Map<String, Object>> viewClassEnrollees(int classId) {
        return dal.searchForEnrolleesByClass(classId);
    }

    //display student classes
    //api: done
    public List<Map<String, Object>> viewStudentClasses(int studentId) {
        return dal.getStudentsClasses(studentId);
    }

    //add reading to a class module
    //partly a placeholder rn, not really the best implementation
    //api: done
    public boolean uploadReading(int instructorId, int classId, String readingName, String filePath) {
        try {
            if (filePath == null || !filePath.isEmpty()) {
                System.out.println("Invalid reading file.");
                return false;
            }

            boolean readingInserted = dal.insertNewReading(instructorId, classId, readingName, filePath);

            if (!readingInserted) {
                System.out.println("Failed.");
                return false;
            }
    
            System.out.println("success");
            return true;

        } catch (Exception e) {
            System.out.println("Error uploading reading.");
            e.printStackTrace();
            return false;
        }
    }

    //add reading objective + profs will enter it manually for now
    //stretch goal: ml gets them from the reading
    //api: done
    public boolean insertNewReadingObjective(int readingId, int classId, String objectiveName) {
        if (objectiveName == null || objectiveName.isEmpty()) {
        return false;
        }
        return dal.insertNewReadingObjective(readingId, classId, objectiveName);
    }

    //just link reading to quiz without returning their objectives
    //api: done
    public boolean addReadingToQuiz(int quizId, int readingId) {
        return dal.insertQuizReading(quizId, readingId);
    }

    //link a reading to a quiz AND return the corresponding objectives
    public List<Map<String, Object>> addReadingToQuizReturn(int quizId, int readingId) {
        boolean success = dal.insertQuizReading(quizId, readingId);
        if (success) {
            return null;
        }
        else {
            return dal.getObjectivesByQuiz(quizId);
        }
    }

    //creates quiz- really just the name
    public int createQuiz(String quizName, int instructorId, int classId) {
        return dal.insertNewQuiz(quizName, instructorId, classId);
    }

    //adds a question to a quiz
    //api: done
    public boolean addQuestionToQuiz(QuestionData questionData) {
        try {
            //keeps track of quiz number- need to verify that this works under multiple cases
            int nextQuestionNumber = dal.getNextQuestionNumberForQuiz(questionData.getQuizId());
            return dal.insertNewQuestion(questionData, nextQuestionNumber);

        } catch (Exception e) {
            System.out.println("Failed to add question to quiz.");
            e.printStackTrace();
            return false;
        }
    }

    //display quizzes
    //api: done
    public List<Map<String, Object>> viewQuizzesByClass(int classId) {
        return dal.getQuizzesByClass(classId);
    }

    //display relavant learning objectives based on the quiz
    //api: done
    public List<Map<String, Object>> viewObjectivesByQuiz(int quizId) {
        return dal.getObjectivesByQuiz(quizId);
    }

    //display ALLLL quiz questions- probably only used on instructor side
    //api: done
    public List<Map<String, Object>> getQuizQuestions(int quizId) {
        List<Map<String, Object>> questions = dal.getQuizQuestions(quizId);

        if (questions == null || questions.isEmpty()) {
            return null;
        } else {
            System.out.println("Loaded " + questions.size() + " questions for quiz ID: " + quizId);
        }
        return questions;
    }

    //student chooses learning objectives
    //api: done
    public boolean selectObjectiveForStudent(int studentId, int quizId, int objectiveId) {
        try {
            return dal.chooseLearningObjective(studentId, quizId, objectiveId);
        } catch (Exception e) {
            System.out.println("Failed to select learning objective.");
            e.printStackTrace();
            return false;
        }
    }

    //return student objectives (needs to get sent to quiz taking process so it can choose questions)
    //api: done
    public List<Integer> getStudentObjectives(int studentId) {
        return dal.getStudentObjective(studentId);
    }

    //calculate and group quiz questions for specific student
    public List<QuizQuestion> getStudentQuizQuestions(int studentId, int quizId, int numQuestions) {
        List<Map<String, Object>> allQuestions = dal.getQuizQuestions(quizId);
        List<Integer> studentObjectives = dal.getStudentObjective(studentId);

        //compare objective ids (i want to limit this)
        List<Map<String, Object>> filtered = allQuestions.stream()
                .filter(q -> studentObjectives.contains((Integer) q.get("objectiveId")))
                .collect(Collectors.toList());

        //take subset
        Collections.shuffle(filtered);
        int count = Math.min(numQuestions, filtered.size());
        List<Map<String, Object>> subset = filtered.subList(0, count);

        //stick them in quiz questions object
        Map<Integer, QuizQuestion> questionMap = new HashMap<>();
        List<QuizQuestion> studentQuizQuestions = new ArrayList<>();

        for (Map<String, Object> row : subset) {
            int questionNumber = (int) row.get("questionNumber");
            QuizQuestion question = questionMap.get(questionNumber);

            if (question == null) {
                question = new QuizQuestion();
                question.setQuestionId((int) row.get("questionId"));
                question.setQuestionNumber(questionNumber);
                question.setQuestionText((String) row.get("questionText"));
                question.setLearningObjective((String) row.get("learningObjective"));
                question.setDifficulty(DifficultyLevel.valueOf((String) row.get("difficulty")));
                question.setChoices(new ArrayList<>());
                question.setCorrectChoiceId((int) row.get("correctChoiceId"));
                questionMap.put(questionNumber, question);
                studentQuizQuestions.add(question);
            }

            QuizQuestion.Choice choice = new QuizQuestion.Choice(
                    (int) row.get("choiceId"),
                    (String) row.get("choiceLabel"),
                    (String) row.get("choiceText")
            );
            question.getChoices().add(choice);
        }

        return studentQuizQuestions;
    }

     //submit answer

    //get quiz score

    //assign badge check

    // helper to check if choice is correct- will use this soon, its nowehre yet
    private boolean isCorrectChoice(QuizQuestion.Choice choice, QuizQuestion question) {
        return choice.getChoiceId() == question.getCorrectChoiceId();
    }


    //display student's badges
    //api: done
    public List<Map<String, Object>> displayStudentBadges(int studentId) {
        return dal.getStudentBadges(studentId);
    }

    //display all badges available to earn
    //api: done
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
