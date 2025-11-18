package g2m.g2m_backend.controller;

import g2m.g2m_backend.business.BusinessLogic;
import g2m.g2m_backend.business.QuizManager;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.QuizQuestion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class QuizController {

    private final BusinessLogic bl;
    private final QuizManager ql;

    public QuizController(BusinessLogic bl, QuizManager ql) {
        this.bl = bl;
        this.ql = ql;
    }

    // Register a new user: UNTESTED
    //frontend:
    //frontend tested:
    @PostMapping("/users/register")
    public boolean registerUser(@RequestBody Map<String, Object> data) {
        String username = (String) data.get("username");
        String email = (String) data.get("email");
        boolean isInstructor = Boolean.parseBoolean(data.get("isInstructor").toString());
        String major = (String) data.get("major");
        String subject = (String) data.get("schoolSubject");
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");

        return bl.registerUser(username, email, isInstructor, major, subject, firstName, lastName);
    }

    //create class: UNTESTED
    //frontend:
    //frontend tested:
    @PostMapping("/classes/create")
    public boolean createClass(@RequestBody Map<String, Object>data) {
        int classId = Integer.parseInt(data.get("classId").toString());
        //boolean isInstructor = Boolean.parseBoolean(data.get("isInstructor").toString());
        String className = data.get("className").toString();
        String instructorEmail = data.get("instructorEmail").toString();
        return bl.createClass(classId, className, instructorEmail);
    }

    //display a list of instructor classes
    //TESTED: Good!
    //retest: good!
    //frontend:
    //frontend tested:
    @GetMapping("/instructors/{instructorId}/classes")
    public List<Map<String, Object>> viewInstructorClasses(@PathVariable int instructorId) {
        return bl.viewInstructorClasses(instructorId);
    }

    // Enroll student: UNTESTED
    //frontend:
    //frontend tested:
    @PostMapping("/instructors/classes/enroll")
    public boolean enrollStudent(@RequestBody Map<String, Object> data) {
        int classId = Integer.parseInt(data.get("classId").toString());
        String email = (String) data.get("email");
        return bl.enrollStudentInClass(classId, email);
    }

    //List enrollees in a class
    //TESTED: Good!
    //retest: good!
    //frontend: Implemented
    //frontend tested:
    @GetMapping("/classes/{classId}/enrollees")
    public List<Map<String, Object>> viewClassEnrollees(@PathVariable int classId) {
        return bl.viewClassEnrollees(classId);
    }

    //display student classes
    //TESTED: Good!
    //retest: good!
    //frontend:
    //frontend tested:
    @GetMapping("/students/{studentId}/classes")
    public List<Map<String, Object>> viewStudentClasses(@PathVariable int studentId) {
        return bl.viewStudentClasses(studentId);
    }

    //display readings
    //TESTED: Good!
    //frontend:
    //frontend tested:
    @GetMapping("/classes/{classId}/readings")
    public List<Map<String, Object>> getClassReadings(@PathVariable int classId) {
        return bl.getClassReadings(classId);
    }

    //checks if create quiz button can be available
    //TESTED: Good!
    //frontend: Implemented
    //frontend tested:
    @GetMapping("/canCreate/{classId}")
    public Map<String, Boolean> canCreateQuiz(@PathVariable int classId) {
        int userId = 4; //hardcoded for testing, try 1 if you want to test a student
        boolean canCreate = bl.canCreateQuiz(userId, classId);
        return Map.of("canCreate", canCreate);
    }

    //just returns a new quiz id to put it in the query string
    //frontend: Implemented
    //frontend tested:
   @PostMapping("/classes/{classId}/quizzcreation")
    public ResponseEntity<Map<String, Integer>> createQuiz(@PathVariable int classId) {
        int userId = 4; //hardcoded for now
        int newQuizId = bl.createQuiz(userId, classId);

        if (newQuizId > 0) {
            return ResponseEntity.ok(Map.of("quizId", newQuizId));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("quizId", -1));
        }
    }

    //upload reading: UNTESTED
    //frontend:
    //frontend tested:
    @PostMapping("/classes/{classId}/readingupload")
    public boolean uploadReading(@PathVariable int classId, @RequestBody Map<String, Object> data) {
        int instructorId = Integer.parseInt(data.get("instructorId").toString());
        String readingName = (String) data.get("readingName");
        String filePath = (String) data.get("filePath");
        return bl.uploadReading(instructorId, classId, readingName, filePath);
    }

    //add objectives to reading: UNTESTED
    //this needs to be manual for now bc time crucnch
    //frontend: 
    //frontend tested:
    @PostMapping("/readings/{readingId}/objectives")
    public ResponseEntity<Map<String, Object>> addReadingObjective(
            @PathVariable int readingId,
            @RequestBody Map<String, Object> requestBody) {

        int classId = (int) requestBody.get("classId");
        String objectiveName = (String) requestBody.get("objectiveName");

        boolean success = bl.insertNewReadingObjective(readingId, classId, objectiveName);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("status", "error"));
        }
    }


    //instructor links readig to quiz during quiz creation: UNTESTED
    //this basically gets the reading id from the reading they're linking
    //frontend: Implemented
    //frontend tested:
    @PostMapping("/quizzes/{quizId}/readings")
    public ResponseEntity<Map<String, Object>> addQuizReading(@PathVariable int quizId, @RequestBody Map<String, Object> requestBody) {
        int readingId = (int) requestBody.get("readingId");
        boolean success = bl.addReadingToQuiz(quizId, readingId);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("status", "error"));
        }
    }

    //publish quiz (DOES NOT UPDATE QUIZ ID) -> i need to make this in db but i know EXACTLY what i need for that so ill do it soon
    //NOT implemented

    //when an instructor clicks on "Add Question" inside their quiz module, the information they put in for that question gets sent here
    //that info is made into a QuesitonData obj
    //i send that to bl
    //UNTESTED
    //frontend: Implemented
    //frontend tested:
    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<Map<String, Object>> addQuestion(
            @PathVariable int quizId,
            @RequestBody Map<String, Object> requestBody) {

        QuestionData question = new QuestionData(
            (String) requestBody.get("questionText"),
            (String) requestBody.get("difficulty"),
            (String) requestBody.get("choiceA"),
            (String) requestBody.get("choiceB"),
            (String) requestBody.get("choiceC"),
            (String) requestBody.get("choiceD"),
            ((String) requestBody.get("correctAnswer")).charAt(0),
            (int) requestBody.get("objectiveId"),
            quizId 
        );

        boolean success = bl.addQuestionToQuiz(question);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("status", "error"));
        }
    }

    //view quizzes for a class
    //TESTED: Good!
    //retest: good!
    //frontend: Implemented
    //frontend tested:
    @GetMapping("/classes/{classId}/quizzes")
    public List<Map<String, Object>> viewQuizzesByClass(@PathVariable int classId) {
        return bl.viewQuizzesByClass(classId);
    }

    //display relavant learning objectives
    //TESTED: good!
    //frontend:
    //frontend tested:
    @GetMapping("/quizzes/{quizId}/objectives")
    public List<Map<String, Object>>viewObjectivesByQuiz(@PathVariable int quizId) {
        return bl.viewObjectivesByQuiz(quizId);
    }

    //display reading objectives for dropdown menu
    //TESTED: Good!
    //frontend: Implemented
    //frontend tested:
    @GetMapping("/quizzes/{readingId}/readingobjectives")
    public List<Map<String, Object>> viewReadingObjectives(@PathVariable int readingId) {
        return bl.viewReadingObjectives(readingId);
    }


    //student selects objectives that were displayed in viewObjectivesByQuiz and they are then sent here
    //UNTESTED
    //frontend:
    //frontend tested:
    @PostMapping("/quizzes/{quizId}/objectives")
    public ResponseEntity<Map<String, Object>> selectObjective(
            @PathVariable int quizId,
            @RequestBody Map<String, Object> requestBody) {

        int studentId = (int) requestBody.get("studentId");
        int objectiveId = (int) requestBody.get("objectiveId");

        boolean success = bl.selectObjectiveForStudent(studentId, quizId, objectiveId);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error"));
        }
    }

    //display chosen objectives
    //if gio wants to add a section where the student can see what they chose, not urgent
    //TESTED: good!
    //frontend:
    //frontend tested:
    @GetMapping("/quizzes/student/{studentId}/objectives")
    public List<Map<String, Object>> viewStudentObjectives(@PathVariable int studentId) {
        return bl.getStudentObjectives(studentId);
    }

    //backend gcalculates a subset of questions for student, make sure these can be displayed
    //frontend:
    //frontend tested:
    @GetMapping("/quizzes/student/{studentId}/{quizId}/questions")
    public List<QuizQuestion> viewStudentQuestions(
            @PathVariable int studentId,
            @PathVariable int quizId,
            @RequestParam(defaultValue = "5") int numQuestions
    ) {
        return bl.getStudentQuizQuestions(studentId, quizId, numQuestions);
    }

    //get ALL quiz questions
    //probably wont need since students arent answering all of these but just in case
    //TESTED: Good!
    //retest: good!
    //frontend:
    //frontend tested:
    @GetMapping("/quizzes/{quizId}/questions")
    public List<Map<String, Object>> viewQuizQuestions(@PathVariable int quizId) {
        return bl.getQuizQuestions(quizId);
    }

    /* 
    //submit answer individually
    @PostMapping("/quizzes/submit")
    public Map<String, Object> submitAnswer(@RequestBody AnswerDTO answer) {
        boolean correct = bl.checkAnswer(answer.getQuestionId(), answer.getChoiceId());

        Map<String, Object> response = new HashMap<>();
        response.put("correct", correct);

        return response;
    }
    */


    //display student badges
    //TESTED: Good!
    //retest: good!
    //frontend:
    //frontend tested:
    @GetMapping("/students/{studentId}/badges")
    public List<Map<String, Object>> viewStudentBadges(@PathVariable int studentId) {
        return bl.displayStudentBadges(studentId);
    }

    //view all badges
    //TESTED: Good!
    //retest: good!
    //frontend:
    //frontend tested:
    @GetMapping("/badges")
    public ArrayList<Badge> viewAllBadges() {
        return bl.displayAllBadges();
    }

    //removes class enrollee
    @DeleteMapping("/classes/{classId}/enrollees/{studentId}")
    public boolean removeEnrollee(
            @PathVariable int classId,
            @PathVariable int studentId
    ) {
        return bl.removeEnrollee(classId, studentId);
    }
    
    // Search students: Ready to connect
    //TESTED: Error, but not a big deal i never use this
    //frontend:
    //frontend tested:
    @GetMapping("/students/search")
    public ArrayList<HashMap<String, Object>> searchStudent(@RequestParam String type, @RequestParam String query) {
        return bl.searchStudent(type, query);
    }
}
