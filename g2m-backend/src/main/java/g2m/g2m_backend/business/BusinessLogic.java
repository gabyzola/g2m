package g2m.g2m_backend.business;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion;
import g2m.g2m_backend.DAL.javaSQLobjects.User;
import g2m.g2m_backend.DAL.QuizDal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessLogic {

    final QuizDal dal;

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
    }

    public User findUserByEmail(String email) {
        try {
            return dal.findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createUser(String email, boolean isInstructor,
                              String major, String schoolSubject,
                              String firstName, String lastName) 
    {
        return dal.insertNewUser(email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    public User getOrCreateUser(String email, boolean isInstructor,
                                String major, String schoolSubject,
                                String firstName, String lastName) 
    {
        return dal.getOrCreateUser(email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    public int getUserIdByEmail(String email) {
        return dal.lookupUserId(email);
    }

    public int getUserRole(int userId) {
        return dal.getUserRole(userId);
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
    public int uploadReading(int instructorId, int classId, String readingName) {
        try {
            int readingId = dal.insertNewReading(instructorId, classId, readingName);
            return readingId;   // already correct: if -1, failure
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //add reading objective + profs will enter it manually for now
    //stretch goal: ml gets them from the reading
    //api: done
    public int insertNewReadingObjective(int readingId, int classId, String objectiveName) {
        if (objectiveName == null || objectiveName.isEmpty()) {
            return -1;
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
    public int createQuiz(int instructorId, int classId) {
        return dal.insertQuiz(instructorId, classId);
    }

    //updates quiz name
    public boolean updateQuizName(int quizId, String newName) {
        return dal.updateQuizName(quizId, newName);
    }


    public List<Map<String, Object>> getClassReadings(int classId) {
        return dal.getClassReadings(classId);
    }

    public List<Map<String, Object>> viewReadingObjectives(int readingId) {
        return dal.getReadingObjectives(readingId);
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
    public List<Map<String, Object>> getStudentObjectives(int studentId) {
        return dal.getStudentObjectives(studentId);
    }

    //calculate and group quiz questions for specific student
    public List<QuizQuestion> getStudentQuizQuestions(int studentId, int quizId) {
        List<Map<String, Object>> allRows = dal.getQuizQuestions(quizId);
        System.out.println("All quiz rows: " + allRows.size());
        List<Map<String, Object>> studentObjectivesRaw = dal.getStudentObjectives(studentId);
        List<Integer> studentObjectiveIds = studentObjectivesRaw.stream()
                .map(o -> ((Number) o.get("objectiveId")).intValue())  // ensure type safety
                .collect(Collectors.toList());
        System.out.println("Student objectives: " + studentObjectiveIds);

        List<Map<String, Object>> filtered = allRows.stream()
                .filter(row -> studentObjectiveIds.contains(((Number) row.get("objectiveId")).intValue()))
                .collect(Collectors.toList());
        System.out.println("Filtered questions: " + filtered.size());
        Collections.shuffle(filtered);


        Map<Integer, QuizQuestion> grouped = new HashMap<>();

        for (Map<String, Object> row : filtered) {
            int qid = ((Number) row.get("questionId")).intValue();
            grouped.putIfAbsent(qid, new QuizQuestion());
            QuizQuestion q = grouped.get(qid);

            q.setQuestionId(qid);
            q.setQuestionText((String) row.get("questionText"));
            q.setObjectiveId(((Number) row.get("objectiveId")).intValue());
            q.setLearningObjective((String) row.get("learningObjective"));

            String diffStr = ((String) row.get("difficulty")).toUpperCase();
            try {
                q.setDifficulty(DifficultyLevel.valueOf(diffStr));
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown difficulty: " + diffStr + ", defaulting to EASY");
                q.setDifficulty(DifficultyLevel.EASY);
            }

            q.setCorrectChoiceId(((Number) row.get("correctChoiceId")).intValue());

            List<QuizQuestion.Choice> choices = q.getChoices();
            if (choices == null) {
                choices = new ArrayList<>();
                q.setChoices(choices);
            }

            QuizQuestion.Choice choice = new QuizQuestion.Choice(
                    ((Number) row.get("choiceId")).intValue(),
                    (String) row.get("choiceLabel"),
                    (String) row.get("choiceText")
            );

            choices.add(choice);
        }

        List<QuizQuestion> result = new ArrayList<>(grouped.values());
        System.out.println("Returning " + result.size() + " questions.");
        return result;
    }


    //checks if user can create a quiz
    public boolean canCreateQuiz(int userId, int classId) {
        return dal.canCreateQuiz(userId, classId);
    }

    public String getClassName(int classId) {
        return dal.getClassName(classId);
    }


    //get questions and choices

     //submit answer

    //get quiz score

    //assign badge check
    public boolean assignBadge(int studentId) {
        return dal.assignBadge(studentId);
    }

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

    //delete class enrollee
    public boolean removeEnrollee(int classId, int studentId) {
        return dal.deleteClassEnrollee(classId, studentId);
    }

    /*misc*/

    //search for a student by email, name, or ID
    public ArrayList<HashMap<String, Object>> searchStudent(String filterType, String query) {
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
