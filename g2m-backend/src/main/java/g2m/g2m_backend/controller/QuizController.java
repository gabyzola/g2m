package g2m.g2m_backend.controller;

import g2m.g2m_backend.DAL.QuizDal;
import g2m.g2m_backend.business.BusinessLogic;
import g2m.g2m_backend.DAL.javaSQLobjects.Badge;
import g2m.g2m_backend.DAL.javaSQLobjects.QuestionData;
import g2m.g2m_backend.DAL.javaSQLobjects.Student;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class QuizController {

    private final BusinessLogic bl;
    //Scanner in = new Scanner(System.in); //not sure if i need this but it gets rid of errors

    public QuizController(BusinessLogic bl) {
        //QuizDal dal = new QuizDal();
        this.bl = bl;
    }

    // Register a new user: Ready to connect
    @PostMapping("/register")
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

    //create class: Ready to connect
    @PostMapping("/class")
    public boolean createClass(@RequestBody Map<String, Object>data) {
        int classId = Integer.parseInt(data.get("classId").toString());
        boolean isInstructor = Boolean.parseBoolean(data.get("isInstructor").toString());
        String className = data.get("className").toString();
        String instructorEmail = data.get("instructorEmail").toString();
        return bl.createClass(classId, className, instructorEmail);
    }

    //display a list of instructor classes
    //TESTED: Good!
    @GetMapping("/instructors/{instructorId}/classes")
    public List<Map<String, Object>> viewInstructorClasses(@PathVariable int instructorId) {
        return bl.viewInstructorClasses(instructorId);
    }

    // Enroll student: NOT ready
    @PostMapping("/enroll")
    public boolean enrollStudent(@RequestBody Map<String, Object> data) {
        int classId = Integer.parseInt(data.get("classId").toString());
        String email = (String) data.get("email");
        return bl.enrollStudentInClass(classId, email);
    }

    //List enrollees in a class
    //TESTED: Error
    @GetMapping("/classes/{classId}/enrollees")
    public List<Map<String, Object>> viewClassEnrollees(@PathVariable int classId) {
        return bl.viewClassEnrollees(classId);
    }

    //display student classes
    //TESTED: Good!
    @GetMapping("/students/{studentId}/classes")
    public List<Map<String, Object>> viewStudentClasses(@PathVariable int studentId) {
        return bl.viewStudentClasses(studentId);
    }

    //upload reading: NOT ready
    @PostMapping("/classes/{classId}/readings")
    public boolean uploadReading(@PathVariable int classId, @RequestBody Map<String, Object> data) {
        int instructorId = Integer.parseInt(data.get("instructorId").toString());
        String readingName = (String) data.get("readingName");
        String filePath = (String) data.get("filePath");
        return bl.uploadReading(instructorId, classId, readingName, filePath);
    }

    //create quiz: NOT ready
    @PostMapping("/classes/{classId}/quiz")
    public boolean createQuiz(@PathVariable int classId, @RequestBody Map<String, Object> data) {
        String quizName = (String) data.get("quizName");
        int instructorId = Integer.parseInt(data.get("instructorId").toString());
        List<Integer> readingIds = (List<Integer>) data.get("readingIds");
        List<QuestionData> questions = (List<QuestionData>) data.get("questions");
        return bl.createQuiz(quizName, instructorId, classId, readingIds, questions);
    }

    //view quizzes for a class
    //TESTED: Good!
    @GetMapping("/classes/{classId}/quizzes")
    public List<Map<String, Object>> viewQuizzesByClass(@PathVariable int classId) {
        return bl.viewQuizzesByClass(classId);
    }

    // Search students: Ready to connect
    //TESTED: Error
    @GetMapping("/students/search")
    public ArrayList<Student> searchStudent(@RequestParam String type, @RequestParam String query) {
        return bl.searchStudent(type, query);
    }

    //display relavant learning objectives
    //TESTED: Good!
    @GetMapping("/quizzes/{quizId}/objectives")
    public List<Map<String, Object>>viewObjectivesByQuiz(@PathVariable int quizId) {
        return bl.viewObjectivesByQuiz(quizId);
    }

    //take quiz: NOT ready
    @PostMapping("/quizzes/{quizId}/take/{studentId}")
    public Map<String, Object> takeQuiz(
            @PathVariable int quizId,
            @PathVariable int studentId,
            @RequestBody Map<String, Object> requestBody) {

        // Expecting something like: { "answers": [1, 3, 2, 4] }
        List<Integer> answers = (List<Integer>) requestBody.get("answers");

        // Call business logic
        return bl.takeQuiz(studentId, quizId, answers);
    }


    //display student badges
    //TESTED: Good
    @GetMapping("/students/{studentId}/badges")
    public List<Map<String, Object>> viewStudentBadges(@PathVariable int studentId) {
        return bl.displayStudentBadges(studentId);
    }

    //view all badges
    //TESTED: Good!
    @GetMapping("/badges")
    public ArrayList<Badge> viewAllBadges() {
        return bl.displayAllBadges();
    }
}
