package g2m.g2m_backend.business;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import g2m.g2m_backend.DAL.QuizDal;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.User;

@Service
public class BusinessLogic {

    final QuizDal dal;

    public BusinessLogic(QuizDal dal) {
        this.dal = dal;
    }

    //either gets the user or creates a new user if they dont exist in db
    //api: done
    public User getOrCreateUser(String googleSub, String email, boolean isInstructor,
                                String major, String schoolSubject,
                                String firstName, String lastName) 
    {
        return dal.getOrCreateUser(googleSub, email, isInstructor, major, schoolSubject, firstName, lastName);
    }

    //looks up the user id based on email: helpful for mapping
    //api: done
    public int getUserIdByEmail(String email) {
        return dal.lookupUserIdByEmail(email);
    }

    //looks up the user id based on googleSub: crucial for the entire app
    //api: done
    public Map<String, Object> getUserIdBySub(String googleSub) {
        return dal.lookupUserIdBySub(googleSub);
    }

    //determines student or instructor
    //api: done
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

    //add reading (topic) to a class module
    //STRETCH GOAL: drop in pdf from files (not happening rn lol got pushed back too much)
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
    //STRETCH GOAL: ml gets them from the reading (100% not happening rn)
    //api: done
    public int insertNewReadingObjective(int readingId, int classId, String objectiveName) {
        if (objectiveName == null || objectiveName.isEmpty()) {
            return -1;
        }
        return dal.insertNewReadingObjective(readingId, classId, objectiveName);
    }

    //just link reading to quiz (so quiz can get objectives)
    //api: done
    public boolean addReadingToQuiz(int quizId, int readingId) {
        return dal.insertQuizReading(quizId, readingId);
    }

    //creates quiz -> just stifcks it in db, there are a lot of tiny methods here
    //api: done
    public int createQuiz(int instructorId, int classId) {
        return dal.insertQuiz(instructorId, classId);
    }

    //updates quiz name -> just the id is in the db right now
    //api: done
    public boolean updateQuizName(int quizId, String newName) {
        return dal.updateQuizName(quizId, newName);
    }

    //gets readings for a class (to display in sidebar)
    //api: done
    public List<Map<String, Object>> getClassReadings(int classId) {
        return dal.getClassReadings(classId);
    }

    //gets reading objectives -> to populate when a prof is making a quiz
    //api: done
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
    //api: done
    public List<QuizQuestion> getStudentQuizQuestions(int studentId, int quizId) {

        //get all questions from a quiz and store them in a map
        List<Map<String, Object>> allRows = dal.getQuizQuestions(quizId);
        System.out.println("All quiz rows: " + allRows.size());
        
        //gets the students objectives from the database
        List<Map<String, Object>> studentObjectivesRaw = dal.getStudentObjectives(studentId);

        //take only the ids for future comparison
        List<Integer> studentObjectiveIds = studentObjectivesRaw.stream()
                .map(o -> ((Number) o.get("objectiveId")).intValue())  //type safety fix
                .collect(Collectors.toList());
        System.out.println("Student objectives: " + studentObjectiveIds);

        //filters all questions to match the student's objectives
        List<Map<String, Object>> filtered = allRows.stream().filter(row -> studentObjectiveIds.contains(((Number) row.get("objectiveId")).intValue())).collect(Collectors.toList());
        System.out.println("Filtered questions: " + filtered.size());
        Collections.shuffle(filtered);
        Map<Integer, QuizQuestion> grouped = new HashMap<>();

        //for every filtered question, set the information in a QuizQuestion object
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

        //return the result to the frontend
        List<QuizQuestion> result = new ArrayList<>(grouped.values());
        System.out.println("Returning " + result.size() + " questions.");
        return result;
    }

    //checks if user can create a quiz -> similar to get user role
    //api: done
    public boolean canCreateQuiz(int userId, int classId) {
        return dal.canCreateQuiz(userId, classId);
    }

    //populates class name field
    //api: done
    public String getClassName(int classId) {
        return dal.getClassName(classId);
    }

    //assign badge check
    //api: 
    public String assignBadge(int studentId) {
        return dal.assignBadge(studentId);
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
    //api: done
    public boolean removeEnrollee(int classId, int studentId) {
        return dal.deleteClassEnrollee(classId, studentId);
    }

    public boolean resetObjectives(int userId) {
        return dal.deleteStudentObjective(userId);
    }

    public boolean deleteUser(int userId) {
        return dal.deleteUser(userId);
    }

    public boolean deleteQuiz(int quizId) {
        return dal.deleteQuiz(quizId);
    }

    //begin attempt session
    //api: done
    public int startAttemptSession(int studentId, int quizId, int objectiveId) {
        try {
            int sessionId = dal.startAttemptSession(studentId, quizId, objectiveId);

            if (sessionId <= 0) {
                System.out.println("Failed to create attempt session.");
            } else {
                System.out.println("Created attempt session: " + sessionId);
            }

            return sessionId;

        } catch (Exception e) {
            System.out.println("Error starting attempt session.");
            e.printStackTrace();
            return -1;
        }
    }

    //save a single answer during the quiz
    //api: done
    public boolean saveStudentAnswer(int sessionId, int questionId, int chosenChoiceId) {
        try {
            boolean success = dal.saveStudentAnswer(sessionId, questionId, chosenChoiceId);

            if (!success) {
                System.out.println("Failed to save answer for questionId: " + questionId);
            }

            return success;

        } catch (Exception e) {
            System.out.println("Error saving student answer.");
            e.printStackTrace();
            return false;
        }
    }

    //ends quiz attempt
    // api: done
    public boolean finalizeAttemptSession(int sessionId) {
        try {
            boolean success = dal.finalizeAttemptSession(sessionId);

            if (success) {
                System.out.println("Session finalized: " + sessionId);
            } else {
                System.out.println("Failed to finalize session: " + sessionId);
            }

            return success;

        } catch (Exception e) {
            System.out.println("Error finalizing attempt session.");
            e.printStackTrace();
            return false;
        }
    }

    //gets results for attempt 
    //api: done (frontend fixed)
    public Map<String, Object> getSessionResults(int sessionId) {
        try {
            Map<String, Object> results = dal.getSessionResults(sessionId);

            if (results == null || results.isEmpty()) {
                System.out.println("No results found for sessionId: " + sessionId);
            } else {
                System.out.println("Loaded results for sessionId: " + sessionId);
            }

            return results;

        } catch (Exception e) {
            System.out.println("Error retrieving session results.");
            e.printStackTrace();
            return Map.of();
        }
    }

    //gets last session id for a student (fixes a frontend problem in results.html)
    //api: done
    public Integer getLatestSessionForStudent(int studentId) {
        return dal.getLatestSessionId(studentId);
    }


    /*misc*/

    //unused procedures (for now?)
    public User findUserByEmail(String email) {
        try {
            return dal.findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // helper to check if choice is correct- will use this soon, its nowehre yet
    private boolean isCorrectChoice(QuizQuestion.Choice choice, QuizQuestion question) {
        return choice.getChoiceId() == question.getCorrectChoiceId();
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

    public boolean createUser(String googleSub, String email, boolean isInstructor,
                              String major, String schoolSubject,
                              String firstName, String lastName) 
    {
        return dal.insertNewUser(googleSub, email, isInstructor, major, schoolSubject, firstName, lastName);
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
