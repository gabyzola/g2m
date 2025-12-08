package g2m.g2m_backend.controller;

import g2m.g2m_backend.business.BusinessLogic;
import g2m.g2m_backend.business.QuizManager;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import g2m.g2m_backend.DAL.javaSQLobjects.User;
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

    //to be used in registering a user or returning an existing user
    //js: 
    @PostMapping("/users/register")
    public User registerUser(@RequestBody Map<String, Object> data) {

        String googleSub = data.get("googleSub").toString();
        String email = data.get("email").toString();
        boolean isInstructor = Boolean.parseBoolean(data.get("isInstructor").toString());
        String major = data.getOrDefault("major", "").toString();
        String schoolSubject = data.getOrDefault("schoolSubject", "").toString();
        String firstName = data.get("firstName").toString();
        String lastName = data.get("lastName").toString();
        System.out.println("Payload received: " + data);

        return bl.getOrCreateUser(
                googleSub, email,
                isInstructor,
                major,
                schoolSubject,
                firstName,
                lastName
        );

    }
    
    //for professor to create a new class
    //js: done
    @PostMapping("/classes/create")
    public boolean createClass(@RequestBody Map<String, Object>data) {
        int classId = Integer.parseInt(data.get("classId").toString());
        //boolean isInstructor = Boolean.parseBoolean(data.get("isInstructor").toString());
        String className = data.get("className").toString();
        String instructorEmail = data.get("instructorEmail").toString();
        return bl.createClass(classId, className, instructorEmail);
    }

    //gets a user from email
    //js: done
    @PostMapping("/lookup")
    public Map<String, Object> lookupUser(@RequestBody Map<String, Object> data) {
        System.out.println("LOOKUP HIT — Incoming body: " + data);

        String email = data.get("email").toString();
        System.out.println("Extracted email: " + email);

        int userId = bl.getUserIdByEmail(email);
        System.out.println("Result from getUserIdByEmail: " + userId);

        return Map.of("userId", userId);
    }
    
    //lookup user by google sub
    //js:
    @PostMapping("/lookup/sub")
    public Map<String, Object> lookupUserBySub(@RequestBody Map<String, Object> data) {
        System.out.println("LOOKUP BY SUB HIT — Incoming body: " + data);

        String googleSub = data.get("googleSub").toString();
        System.out.println("Extracted googleSub: " + googleSub);

        Map<String, Object> userData = bl.getUserIdBySub(googleSub);
        System.out.println("Result from getUserIdBySub: " + userData);

        return userData; // returns both userId and email
    }

    //display a list of instructor classes
    //TESTED: Good!
    //retest: good!
    //js:
    @GetMapping("/instructors/{instructorId}/classes")
    public List<Map<String, Object>> viewInstructorClasses(@PathVariable int instructorId) {
        return bl.viewInstructorClasses(instructorId);
    }

    // Enroll student: Tested
    //js:
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
    public Map<String, Boolean> canCreateQuiz(
            @PathVariable int classId,
            @RequestParam int userId) {   //no more hardcoded id wooo!
        boolean canCreate = bl.canCreateQuiz(userId, classId);
        return Map.of("canCreate", canCreate);
    }

    //gets whether student or instructor
    @GetMapping("/role/{userId}")
    public Map<String, Object> getUserRole(@PathVariable int userId) {
        int role = bl.getUserRole(userId); //1 = instructor, 0 = student, -1 = not found/error
        return Map.of("isInstructor", role == 1);
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
    public Map<String, Object> uploadReading(@PathVariable int classId,
                                            @RequestBody Map<String, Object> data) {

        int instructorId = Integer.parseInt(data.get("instructorId").toString());
        String readingName = data.get("readingName").toString();

        int readingId = bl.uploadReading(instructorId, classId, readingName);

        return Map.of("readingId", readingId);
    }

    //add objectives to reading: UNTESTED
    //this needs to be manual for now bc time crucnch
    //frontend: 
    //frontend tested:
    @PostMapping("/readings/{readingId}/objectives")
    public ResponseEntity<Map<String, Object>> addReadingObjective(
            @PathVariable int readingId,
            @RequestBody Map<String, Object> requestBody) {

        int classId = Integer.parseInt(requestBody.get("classId").toString());
        String objectiveName = requestBody.get("objectiveName").toString();

        int insertedId = bl.insertNewReadingObjective(readingId, classId, objectiveName);

        if (insertedId > 0) {
            return ResponseEntity.ok(Map.of("status", "success", "objectiveId", insertedId));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("status", "error"));
        }
    }

    //call this when a student submits quiz, checks whether they deserve a new badge
    @PostMapping("/students/{studentId}/badgeAssign")
    public boolean assignBadge(@PathVariable int studentId) {
        return bl.assignBadge(studentId);
    }

    //instructor links readig to quiz during quiz creation: UNTESTED
    //this basically gets the reading id from the reading they're linking
    //frontend: Implemented
    //frontend tested:
    @PostMapping("/quiz/{quizId}/addReading")
    public Map<String, Object> addReadingToQuiz(
            @PathVariable int quizId,
            @RequestBody Map<String, Object> data) {

        int readingId = Integer.parseInt(data.get("readingId").toString());

        boolean success = bl.addReadingToQuiz(quizId, readingId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    //when an instructor clicks on "Add Question" inside their quiz module, the information they put in for that question gets sent here
    //i send that to bl
    //js: done
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

    //updates the quiz name
    //js: done
    @PostMapping("/quizzes/{quizId}/name")
    public ResponseEntity<Map<String, Object>> updateQuizName(
            @PathVariable int quizId,
            @RequestBody Map<String, Object> requestBody) {

        String newName = (String) requestBody.get("quizName");
        boolean success = bl.updateQuizName(quizId, newName);

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

        System.out.println("=== Incoming POST to selectObjective ===");
        System.out.println("Quiz ID from path: " + quizId);
        System.out.println("Request Body: " + requestBody);

        // Safely parse integers from request body
        int studentId;
        int objectiveId;
        try {
            studentId = Integer.parseInt(requestBody.get("studentId").toString());
            objectiveId = Integer.parseInt(requestBody.get("objectiveId").toString());
        } catch (Exception e) {
            System.out.println("Failed to parse studentId or objectiveId: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error", "message", "Invalid IDs"));
        }

        System.out.println("Parsed studentId: " + studentId);
        System.out.println("Parsed objectiveId: " + objectiveId);

        // Call your business logic
        boolean success = bl.selectObjectiveForStudent(studentId, quizId, objectiveId);
        System.out.println("BL result = " + success);

        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("status", "error", "message", "BL returned false"));
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
    //async: done
    //frontend:
    //frontend tested:
    @GetMapping("/quizzes/student/{studentId}/{quizId}/questions")
    public List<QuizQuestion> viewStudentQuestions(
            @PathVariable int studentId,
            @PathVariable int quizId
    ) {
        return bl.getStudentQuizQuestions(studentId, quizId);
    }

    //get ALL quiz questions
    //probably wont need since students arent answering all of these but just in case
    //TESTED: Good!
    //retest: good!
    //async: done
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
    //TESTED: Good
    //frontend:
    //frontend tested:
    @GetMapping("/students/search")
    public ArrayList<HashMap<String, Object>> searchStudent(@RequestParam String type, @RequestParam String query) {
        return bl.searchStudent(type, query);
    }

    @GetMapping("/classes/{classId}/name")
    public Map<String, Object> getClassName(@PathVariable int classId) {
        String name = bl.getClassName(classId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("classId", classId);
        resp.put("className", name);

        return resp;
    }
}
